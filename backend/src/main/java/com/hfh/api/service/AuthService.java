package com.hfh.api.service;

import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hfh.api.common.Result;
import com.hfh.api.dto.LoginDTO;
import com.hfh.api.dto.RegisterDTO;
import com.hfh.api.dto.ResetPasswordDTO;
import com.hfh.api.dto.TokenVO;
import com.hfh.api.entity.SysUserEntity;
import com.hfh.api.mapper.SysUserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;

/**
 * 认证服务
 * 双Token机制：
 * - Access Token: Sa-Token管理，短期有效（2小时），用于接口认证
 * - Refresh Token: Redis管理，长期有效（30天），用于静默刷新Access Token
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final SysUserMapper sysUserMapper;
    private final CaptchaService captchaService;
    private final StringRedisTemplate redisTemplate;
    private final BCryptPasswordEncoder passwordEncoder;

    private static final String REFRESH_TOKEN_PREFIX = "auth:refresh:";
    private static final long REFRESH_TOKEN_TTL = 2592000L; // 30天（秒）

    /**
     * 用户登录
     */
    public Result<TokenVO> login(LoginDTO loginDTO) {
        // 1. 根据用户名或邮箱查询用户
        SysUserEntity user = sysUserMapper.selectOne(
                new LambdaQueryWrapper<SysUserEntity>()
                        .eq(SysUserEntity::getUsername, loginDTO.getAccount())
                        .or()
                        .eq(SysUserEntity::getEmail, loginDTO.getAccount())
        );

        // 2. 验证用户是否存在
        if (user == null) {
            return Result.fail(401, "账号或密码错误");
        }

        // 3. 验证密码
        if (!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
            return Result.fail(401, "账号或密码错误");
        }

        // 4. 验证账号状态
        if (user.getStatus() == 0) {
            return Result.fail(403, "账号已被禁用，请联系管理员");
        }

        // 5. 执行登录，生成Access Token
        StpUtil.login(user.getId());
        SaTokenInfo tokenInfo = StpUtil.getTokenInfo();

        // 6. 生成Refresh Token
        TokenVO tokenVO = buildTokenVO(tokenInfo, user.getId());

        return Result.ok(tokenVO);
    }

    /**
     * 用户注册（默认为普通用户角色）
     */
    public Result<TokenVO> register(RegisterDTO registerDTO) {
        // 0. 校验邮箱验证码
        if (!captchaService.verifyEmailCode(registerDTO.getEmail(), registerDTO.getCode())) {
            return Result.fail(400, "验证码错误或已过期");
        }

        // 1. 检查用户名是否已存在
        Long count = sysUserMapper.selectCount(
                new LambdaQueryWrapper<SysUserEntity>()
                        .eq(SysUserEntity::getUsername, registerDTO.getUsername())
        );
        if (count > 0) {
            return Result.fail(400, "用户名已存在");
        }

        // 2. 检查邮箱是否已存在
        count = sysUserMapper.selectCount(
                new LambdaQueryWrapper<SysUserEntity>()
                        .eq(SysUserEntity::getEmail, registerDTO.getEmail())
        );
        if (count > 0) {
            return Result.fail(400, "邮箱已被注册");
        }

        // 3. 创建新用户
        SysUserEntity user = new SysUserEntity();
        user.setUsername(registerDTO.getUsername());
        user.setPassword(passwordEncoder.encode(registerDTO.getPassword()));
        user.setEmail(registerDTO.getEmail());
        user.setNickname(registerDTO.getNickname() != null ? registerDTO.getNickname() : registerDTO.getUsername());
        user.setRole(0); // 默认为普通用户
        user.setStatus(1); // 默认正常状态

        int rows = sysUserMapper.insert(user);
        if (rows > 0) {
            // 注册成功后自动登录
            StpUtil.login(user.getId());
            SaTokenInfo tokenInfo = StpUtil.getTokenInfo();
            TokenVO tokenVO = buildTokenVO(tokenInfo, user.getId());
            return Result.ok(tokenVO);
        }
        return Result.fail(500, "注册失败，请稍后重试");
    }

    /**
     * 使用Refresh Token刷新Access Token
     * 前端通过Refresh-Token请求头传递Refresh Token
     */
    public Result<TokenVO> refreshToken(String refreshTokenValue) {
        // 1. 校验Refresh Token
        if (refreshTokenValue == null || refreshTokenValue.isEmpty()) {
            return Result.fail(401, "缺少Refresh Token，请重新登录");
        }

        String redisKey = REFRESH_TOKEN_PREFIX + refreshTokenValue;
        String userIdStr = redisTemplate.opsForValue().get(redisKey);
        if (userIdStr == null) {
            return Result.fail(401, "Refresh Token已失效，请重新登录");
        }

        // 2. 删除旧的Refresh Token（一次性使用，防止重放攻击）
        redisTemplate.delete(redisKey);

        // 3. 重新登录，生成新的Access Token
        Long userId = Long.valueOf(userIdStr);
        StpUtil.login(userId);
        SaTokenInfo tokenInfo = StpUtil.getTokenInfo();

        // 4. 生成新的Refresh Token
        TokenVO tokenVO = buildTokenVO(tokenInfo, userId);

        return Result.ok(tokenVO);
    }

    /**
     * 用户登出
     */
    public Result<Void> logout(String refreshTokenValue) {
        if (StpUtil.isLogin()) {
            StpUtil.logout();
        }
        // 删除Refresh Token
        if (refreshTokenValue != null && !refreshTokenValue.isEmpty()) {
            redisTemplate.delete(REFRESH_TOKEN_PREFIX + refreshTokenValue);
        }
        return Result.ok(null);
    }

    /**
     * 找回密码（通过邮箱验证码重置）
     */
    public Result<Void> resetPassword(ResetPasswordDTO dto) {
        // 1. 校验邮箱验证码
        if (!captchaService.verifyEmailCode(dto.getEmail(), dto.getCode())) {
            return Result.fail(400, "验证码错误或已过期");
        }

        // 2. 查询用户
        SysUserEntity user = sysUserMapper.selectOne(
                new LambdaQueryWrapper<SysUserEntity>()
                        .eq(SysUserEntity::getEmail, dto.getEmail())
        );
        if (user == null) {
            return Result.fail(400, "该邮箱未注册");
        }

        // 3. 更新密码
        SysUserEntity updateEntity = new SysUserEntity();
        updateEntity.setId(user.getId());
        updateEntity.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        sysUserMapper.updateById(updateEntity);

        return Result.ok(null);
    }

    /**
     * 构建双Token响应
     */
    private TokenVO buildTokenVO(SaTokenInfo tokenInfo, Long userId) {
        // 生成Refresh Token（UUID，存入Redis，30天有效）
        String refreshToken = UUID.randomUUID().toString().replace("-", "");
        String redisKey = REFRESH_TOKEN_PREFIX + refreshToken;
        redisTemplate.opsForValue().set(redisKey, String.valueOf(userId), Duration.ofSeconds(REFRESH_TOKEN_TTL));

        TokenVO tokenVO = new TokenVO();
        tokenVO.setToken(tokenInfo.getTokenValue());
        tokenVO.setExpiresIn(tokenInfo.getTokenTimeout());
        tokenVO.setRefreshToken(refreshToken);
        tokenVO.setRefreshExpiresIn(REFRESH_TOKEN_TTL);
        return tokenVO;
    }
}

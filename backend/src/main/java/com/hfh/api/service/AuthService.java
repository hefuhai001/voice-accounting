package com.hfh.api.service;

import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hfh.api.common.Result;
import com.hfh.api.dto.LoginDTO;
import com.hfh.api.dto.RegisterDTO;
import com.hfh.api.dto.TokenVO;
import com.hfh.api.entity.SysUserEntity;
import com.hfh.api.mapper.SysUserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 认证服务
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final SysUserMapper sysUserMapper;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /**
     * 用户登录
     */
    public Result<TokenVO> login(LoginDTO loginDTO) {
        // 1. 查询用户
        SysUserEntity user = sysUserMapper.selectOne(
                new LambdaQueryWrapper<SysUserEntity>()
                        .eq(SysUserEntity::getUsername, loginDTO.getUsername())
        );

        // 2. 验证用户是否存在
        if (user == null) {
            return Result.fail(401, "用户名或密码错误");
        }

        // 3. 验证密码
        if (!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
            return Result.fail(401, "用户名或密码错误");
        }

        // 4. 验证账号状态
        if (user.getStatus() == 0) {
            return Result.fail(403, "账号已被禁用，请联系管理员");
        }

        // 5. 执行登录（Sa-Token会自动将登录状态存入Redis）
        StpUtil.login(user.getId());

        // 6. 获取Token信息
        SaTokenInfo tokenInfo = StpUtil.getTokenInfo();
        TokenVO tokenVO = new TokenVO(tokenInfo.getTokenValue(), tokenInfo.getTokenTimeout());

        return Result.ok(tokenVO);
    }

    /**
     * 用户注册（默认为普通用户角色）
     */
    public Result<Void> register(RegisterDTO registerDTO) {
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
            return Result.ok(null);
        }
        return Result.fail(500, "注册失败，请稍后重试");
    }

    /**
     * 无感刷新Token
     */
    public Result<TokenVO> refreshToken() {
        // 1. 检查当前是否已登录
        if (!StpUtil.isLogin()) {
            return Result.fail(401, "未登录或Token已过期，请重新登录");
        }

        // 2. 续期Token（延长有效期，不会改变token值）
        StpUtil.updateLastActiveToNow();

        // 3. 获取新的Token信息
        SaTokenInfo tokenInfo = StpUtil.getTokenInfo();
        TokenVO tokenVO = new TokenVO(tokenInfo.getTokenValue(), tokenInfo.getTokenTimeout());

        return Result.ok(tokenVO);
    }

    /**
     * 用户登出
     */
    public Result<Void> logout() {
        if (StpUtil.isLogin()) {
            StpUtil.logout();
        }
        return Result.ok(null);
    }
}

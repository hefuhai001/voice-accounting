package com.hfh.api.user.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hfh.api.common.Result;
import com.hfh.api.entity.SysUserEntity;
import com.hfh.api.service.CaptchaService;
import com.hfh.api.service.ISysUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

/**
 * 用户端-个人中心控制器
 */
@Tag(name = "用户端-个人中心")
@RestController
@RequestMapping("/api/user")
public class UserProfileController {

    @Autowired
    private ISysUserService sysUserService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private CaptchaService captchaService;

    @Operation(summary = "获取用户信息")
    @GetMapping("/info/{userId}")
    public Result<SysUserEntity> getUserInfo(@PathVariable Long userId) {
        SysUserEntity user = sysUserService.getById(userId);
        if (user != null) {
            user.setPassword(null); // 不返回密码
        }
        return Result.ok(user);
    }

    @Operation(summary = "更新用户信息")
    @PutMapping("/{userId}")
    public Result<Boolean> updateUserInfo(
            @PathVariable Long userId,
            @RequestBody SysUserEntity entity,
            @RequestParam(required = false) String emailCode) {
        // 获取当前用户信息
        SysUserEntity currentUser = sysUserService.getById(userId);
        if (currentUser == null) {
            return Result.fail("用户不存在");
        }

        // 如果邮箱变更，必须验证验证码
        if (entity.getEmail() != null && !entity.getEmail().equals(currentUser.getEmail())) {
            if (emailCode == null || emailCode.isEmpty()) {
                return Result.fail("邮箱变更需提供验证码");
            }
            if (!captchaService.verifyEmailCode(entity.getEmail(), emailCode)) {
                return Result.fail("验证码错误或已过期");
            }
        }

        // 只允许更新部分字段
        SysUserEntity updateEntity = new SysUserEntity();
        updateEntity.setId(userId);
        updateEntity.setNickname(entity.getNickname());
        updateEntity.setEmail(entity.getEmail());
        updateEntity.setPhone(entity.getPhone());
        updateEntity.setAvatar(entity.getAvatar());
        return Result.ok(sysUserService.updateById(updateEntity));
    }

    @Operation(summary = "修改密码")
    @PutMapping("/{userId}/password")
    public Result<Boolean> updatePassword(
            @PathVariable Long userId,
            @RequestParam String oldPassword,
            @RequestParam String newPassword) {
        SysUserEntity user = sysUserService.getById(userId);
        if (user == null) {
            return Result.fail("用户不存在");
        }
        // 验证旧密码
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            return Result.fail("原密码错误");
        }
        // 更新新密码
        SysUserEntity updateEntity = new SysUserEntity();
        updateEntity.setId(userId);
        updateEntity.setPassword(passwordEncoder.encode(newPassword));
        return Result.ok(sysUserService.updateById(updateEntity));
    }

    @Operation(summary = "检查邮箱是否已存在")
    @GetMapping("/check-email")
    public Result<Boolean> checkEmail(
            @RequestParam String email,
            @RequestParam(required = false) Long excludeUserId) {
        QueryWrapper<SysUserEntity> wrapper = new QueryWrapper<SysUserEntity>()
                .eq("email", email)
                .ne(excludeUserId != null, "id", excludeUserId);
        long count = sysUserService.count(wrapper);
        return Result.ok(count > 0);
    }
}
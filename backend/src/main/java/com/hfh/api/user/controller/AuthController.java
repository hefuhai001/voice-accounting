package com.hfh.api.user.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;
import com.hfh.api.common.Result;
import com.hfh.api.dto.LoginDTO;
import com.hfh.api.dto.RegisterDTO;
import com.hfh.api.dto.TokenVO;
import com.hfh.api.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制器（用户端）
 * 提供登录、注册、刷新Token等功能
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "认证管理", description = "用户登录、注册、Token刷新等接口")
public class AuthController {

    private final AuthService authService;

    /**
     * 用户登录（管理员和普通用户都可以使用）
     */
    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "支持管理员和普通用户登录")
    public Result<TokenVO> login(@Valid @RequestBody LoginDTO loginDTO) {
        return authService.login(loginDTO);
    }

    /**
     * 用户注册（默认为普通用户角色）
     */
    @PostMapping("/register")
    @Operation(summary = "用户注册", description = "新用户注册，默认为普通用户角色，注册成功后自动登录")
    public Result<TokenVO> register(@Valid @RequestBody RegisterDTO registerDTO) {
        return authService.register(registerDTO);
    }

    /**
     * 无感刷新Token
     */
    @PostMapping("/refresh-token")
    @Operation(summary = "刷新Token", description = "无感刷新访问令牌，延长有效期")
    public Result<TokenVO> refreshToken() {
        return authService.refreshToken();
    }

    /**
     * 用户登出
     */
    @PostMapping("/logout")
    @SaCheckLogin
    @Operation(summary = "用户登出", description = "退出当前登录状态")
    public Result<Void> logout() {
        return authService.logout();
    }

    /**
     * 获取当前登录用户信息
     */
    @GetMapping("/info")
    @SaCheckLogin
    @Operation(summary = "获取当前用户信息", description = "获取当前登录用户的详细信息")
    public Result<String> getUserInfo() {
        Object loginId = StpUtil.getLoginId();
        // 将loginId转为String，避免前端精度丢失
        return Result.ok(String.valueOf(loginId));
    }
}

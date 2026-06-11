package com.hfh.api.config;

import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.stp.StpUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Sa-Token 路由拦截配置
 * 配置哪些路径需要登录认证，哪些路径可以匿名访问
 */
@Configuration
public class SaTokenConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new SaInterceptor(handle -> StpUtil.checkLogin()))
                .addPathPatterns("/**")
                .excludePathPatterns(
                        // Spring Boot 404 转发路径（必须排除，否则上下文被清除后再次进入拦截器会报 SaTokenContext 未初始化）
                        "/error",
                        // 认证相关接口
                        "/api/auth/login",
                        "/api/auth/register",
                        // 验证码相关接口
                        "/api/captcha/**",
                        // Knife4j / SpringDoc 文档路径
                        "/doc.html",
                        "/webjars/**",
                        "/swagger-resources/**",
                        "/v3/api-docs/**",
                        "/favicon.ico"
                );
    }
}

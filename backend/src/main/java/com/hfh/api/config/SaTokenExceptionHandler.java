package com.hfh.api.config;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotPermissionException;
import cn.dev33.satoken.exception.NotRoleException;
import com.hfh.api.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import io.swagger.v3.oas.annotations.Hidden;

/**
 * Sa-Token 全局异常处理
 * 注意：必须指定 basePackages，否则会拦截 Knife4j/SpringDoc 内部接口导致文档失效
 */
@Hidden
@Slf4j
@RestControllerAdvice(basePackages = {"com.hfh.api", "cn.dev33.satoken"})
public class SaTokenExceptionHandler {

    /**
     * 未登录异常
     */
    @ExceptionHandler(NotLoginException.class)
    public Result<Void> handleNotLoginException(NotLoginException e) {
        String message;
        String type = e.getType();
        if (type == null) {
            message = "未登录，请先登录";
        } else {
            switch (type) {
                case "NotToken":
                    message = "未提供Token";
                    break;
                case "InvalidToken":
                    message = "Token无效";
                    break;
                case "TokenTimeout":
                    message = "登录已过期，请重新登录";
                    break;
                case "ActiveTimeout":
                    // activity-timeout过期，但绝对timeout可能未过期，前端可尝试刷新
                    message = "会话过期，请刷新";
                    break;
                case "BeReplaced":
                    message = "账号在别处登录";
                    break;
                case "KickOut":
                    message = "账号被踢下线";
                    break;
                default:
                    message = "未登录，请先登录";
                    break;
            }
        }
        return Result.fail(401, message);
    }

    /**
     * 缺少权限异常
     */
    @ExceptionHandler(NotPermissionException.class)
    public Result<Void> handleNotPermissionException(NotPermissionException e) {
        return Result.fail(403, "缺少权限：" + e.getPermission());
    }

    /**
     * 缺少角色异常
     */
    @ExceptionHandler(NotRoleException.class)
    public Result<Void> handleNotRoleException(NotRoleException e) {
        return Result.fail(403, "缺少角色：" + e.getRole());
    }
}

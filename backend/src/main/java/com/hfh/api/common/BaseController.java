package com.hfh.api.common;

import cn.dev33.satoken.stp.StpUtil;
import com.hfh.api.entity.SysUserEntity;
import com.hfh.api.service.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 控制器基类 - 提供当前登录用户信息获取能力
 */
public class BaseController {

    @Autowired
    private ISysUserService sysUserService;

    /**
     * 获取当前登录用户ID
     */
    protected Long getCurrentUserId() {
        return StpUtil.getLoginIdAsLong();
    }

    /**
     * 获取当前登录用户完整信息
     */
    protected SysUserEntity getCurrentUser() {
        return sysUserService.getById(getCurrentUserId());
    }
}

package com.hfh.api.admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hfh.api.common.Result;
import com.hfh.api.entity.SysUserEntity;
import com.hfh.api.service.ISysUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 管理端-用户管理控制器
 */
@Tag(name = "管理端-用户管理")
@RestController
@RequestMapping("/admin/user")
public class SysUserController {

    @Autowired
    private ISysUserService sysUserService;

    @Operation(summary = "分页查询用户列表")
    @GetMapping("/page")
    public Result<Page<SysUserEntity>> page(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            SysUserEntity query) {
        Page<SysUserEntity> page = sysUserService.page(new Page<>(current, size),
                new QueryWrapper<SysUserEntity>()
                        .like(query.getUsername() != null, "username", query.getUsername())
                        .like(query.getEmail() != null, "email", query.getEmail())
                        .eq(query.getStatus() != null, "status", query.getStatus())
                        .eq(query.getRole() != null, "role", query.getRole())
                        .orderByDesc("created_at"));
        return Result.ok(page);
    }

    @Operation(summary = "根据ID查询用户详情")
    @GetMapping("/{id}")
    public Result<SysUserEntity> getById(@PathVariable Long id) {
        return Result.ok(sysUserService.getById(id));
    }

    @Operation(summary = "新增用户")
    @PostMapping
    public Result<Boolean> save(@RequestBody SysUserEntity entity) {
        return Result.ok(sysUserService.save(entity));
    }

    @Operation(summary = "修改用户信息")
    @PutMapping("/{id}")
    public Result<Boolean> update(@PathVariable Long id, @RequestBody SysUserEntity entity) {
        entity.setId(id);
        return Result.ok(sysUserService.updateById(entity));
    }

    @Operation(summary = "分配角色")
    @PutMapping("/{id}/role")
    public Result<Boolean> assignRole(@PathVariable Long id, @RequestParam Integer role) {
        SysUserEntity entity = new SysUserEntity();
        entity.setId(id);
        entity.setRole(role);
        return Result.ok(sysUserService.updateById(entity));
    }

    @Operation(summary = "禁用/启用用户")
    @PutMapping("/{id}/status")
    public Result<Boolean> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        SysUserEntity entity = new SysUserEntity();
        entity.setId(id);
        entity.setStatus(status);
        return Result.ok(sysUserService.updateById(entity));
    }

    @Operation(summary = "删除用户")
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        return Result.ok(sysUserService.removeById(id));
    }
}

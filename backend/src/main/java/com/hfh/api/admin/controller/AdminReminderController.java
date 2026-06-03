package com.hfh.api.admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hfh.api.common.Result;
import com.hfh.api.entity.ReminderEntity;
import com.hfh.api.service.IReminderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 管理端-提醒管理控制器
 */
@Tag(name = "管理端-提醒管理")
@RestController
@RequestMapping("/admin/reminder")
public class AdminReminderController {

    @Autowired
    private IReminderService reminderService;

    @Operation(summary = "分页查询所有提醒列表")
    @GetMapping("/page")
    public Result<Page<ReminderEntity>> page(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Integer status,
            ReminderEntity query) {
        Page<ReminderEntity> page = reminderService.page(new Page<>(current, size),
                new QueryWrapper<ReminderEntity>()
                        .eq(userId != null, "user_id", userId)
                        .eq(status != null, "status", status)
                        .like(query.getTitle() != null, "title", query.getTitle())
                        .orderByAsc("remind_date"));
        return Result.ok(page);
    }

    @Operation(summary = "根据ID查询提醒详情")
    @GetMapping("/{id}")
    public Result<ReminderEntity> getById(@PathVariable Long id) {
        return Result.ok(reminderService.getById(id));
    }

    @Operation(summary = "修改提醒")
    @PutMapping("/{id}")
    public Result<Boolean> update(@PathVariable Long id, @RequestBody ReminderEntity entity) {
        entity.setId(id);
        return Result.ok(reminderService.updateById(entity));
    }

    @Operation(summary = "关闭提醒")
    @PutMapping("/{id}/close")
    public Result<Boolean> close(@PathVariable Long id) {
        ReminderEntity entity = new ReminderEntity();
        entity.setId(id);
        entity.setStatus(2);
        return Result.ok(reminderService.updateById(entity));
    }

    @Operation(summary = "删除提醒")
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        return Result.ok(reminderService.removeById(id));
    }
}

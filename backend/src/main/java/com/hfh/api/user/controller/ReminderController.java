package com.hfh.api.user.controller;

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
 * 用户端-提醒管理控制器
 */
@Tag(name = "用户端-提醒管理")
@RestController
@RequestMapping("/api/reminder")
public class ReminderController {

    @Autowired
    private IReminderService reminderService;

    @Operation(summary = "分页查询我的提醒列表")
    @GetMapping("/page")
    public Result<Page<ReminderEntity>> page(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam Long userId,
            @RequestParam(required = false) Integer status) {
        Page<ReminderEntity> page = reminderService.page(new Page<>(current, size),
                new QueryWrapper<ReminderEntity>()
                        .eq("user_id", userId)
                        .eq(status != null, "status", status)
                        .orderByAsc("remind_date"));
        return Result.ok(page);
    }

    @Operation(summary = "根据ID查询提醒详情")
    @GetMapping("/{id}")
    public Result<ReminderEntity> getById(@PathVariable Long id) {
        return Result.ok(reminderService.getById(id));
    }

    @Operation(summary = "新增提醒")
    @PostMapping
    public Result<Boolean> save(@RequestBody ReminderEntity entity) {
        return Result.ok(reminderService.save(entity));
    }

    @Operation(summary = "修改提醒")
    @PutMapping("/{id}")
    public Result<Boolean> update(@PathVariable Long id, @RequestBody ReminderEntity entity) {
        entity.setId(id);
        return Result.ok(reminderService.updateById(entity));
    }

    @Operation(summary = "标记提醒已读")
    @PutMapping("/{id}/read")
    public Result<Boolean> markRead(@PathVariable Long id) {
        ReminderEntity entity = new ReminderEntity();
        entity.setId(id);
        entity.setStatus(1);
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

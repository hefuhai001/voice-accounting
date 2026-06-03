package com.hfh.api.admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hfh.api.common.Result;
import com.hfh.api.entity.AccountBookEntity;
import com.hfh.api.service.IAccountBookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 管理端-账本管理控制器
 */
@Tag(name = "管理端-账本管理")
@RestController
@RequestMapping("/admin/book")
public class AdminAccountBookController {

    @Autowired
    private IAccountBookService accountBookService;

    @Operation(summary = "分页查询所有账本列表")
    @GetMapping("/page")
    public Result<Page<AccountBookEntity>> page(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Long userId,
            AccountBookEntity query) {
        Page<AccountBookEntity> page = accountBookService.page(new Page<>(current, size),
                new QueryWrapper<AccountBookEntity>()
                        .eq(userId != null, "user_id", userId)
                        .like(query.getName() != null, "name", query.getName())
                        .eq(query.getType() != null, "type", query.getType())
                        .orderByDesc("created_at"));
        return Result.ok(page);
    }

    @Operation(summary = "根据ID查询账本详情")
    @GetMapping("/{id}")
    public Result<AccountBookEntity> getById(@PathVariable Long id) {
        return Result.ok(accountBookService.getById(id));
    }

    @Operation(summary = "修改账本信息")
    @PutMapping("/{id}")
    public Result<Boolean> update(@PathVariable Long id, @RequestBody AccountBookEntity entity) {
        entity.setId(id);
        return Result.ok(accountBookService.updateById(entity));
    }

    @Operation(summary = "删除账本")
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        return Result.ok(accountBookService.removeById(id));
    }
}

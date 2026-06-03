package com.hfh.api.user.controller;

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
 * 用户端-账本管理控制器
 */
@Tag(name = "用户端-账本管理")
@RestController
@RequestMapping("/api/book")
public class AccountBookController {

    @Autowired
    private IAccountBookService accountBookService;

    @Operation(summary = "分页查询我的账本列表")
    @GetMapping("/page")
    public Result<Page<AccountBookEntity>> page(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam Long userId,
            AccountBookEntity query) {
        Page<AccountBookEntity> page = accountBookService.page(new Page<>(current, size),
                new QueryWrapper<AccountBookEntity>()
                        .eq("user_id", userId)
                        .like(query.getName() != null, "name", query.getName())
                        .eq(query.getType() != null, "type", query.getType())
                        .orderByAsc("sort_order"));
        return Result.ok(page);
    }

    @Operation(summary = "获取我的默认账本")
    @GetMapping("/default")
    public Result<AccountBookEntity> getDefault(@RequestParam Long userId) {
        QueryWrapper<AccountBookEntity> wrapper = new QueryWrapper<AccountBookEntity>()
                .eq("user_id", userId)
                .eq("is_default", 1)
                .last("LIMIT 1");
        return Result.ok(accountBookService.getOne(wrapper));
    }

    @Operation(summary = "根据ID查询账本详情")
    @GetMapping("/{id}")
    public Result<AccountBookEntity> getById(@PathVariable Long id) {
        return Result.ok(accountBookService.getById(id));
    }

    @Operation(summary = "新建账本")
    @PostMapping
    public Result<Boolean> save(@RequestBody AccountBookEntity entity) {
        return Result.ok(accountBookService.save(entity));
    }

    @Operation(summary = "修改账本")
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

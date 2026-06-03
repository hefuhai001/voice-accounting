package com.hfh.api.admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hfh.api.common.Result;
import com.hfh.api.entity.TransactionEntity;
import com.hfh.api.service.ITransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

/**
 * 管理端-记账记录管理控制器（查看所有用户记账数据）
 */
@Tag(name = "管理端-记账记录")
@RestController
@RequestMapping("/admin/transaction")
public class AdminTransactionController {

    @Autowired
    private ITransactionService transactionService;

    @Operation(summary = "分页查询所有记账记录")
    @GetMapping("/page")
    public Result<Page<TransactionEntity>> page(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Long bookId,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Integer type,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        QueryWrapper<TransactionEntity> wrapper = new QueryWrapper<TransactionEntity>()
                .eq(bookId != null, "book_id", bookId)
                .eq(categoryId != null, "category_id", categoryId)
                .eq(type != null, "type", type)
                .ge(startDate != null, "transaction_date", startDate)
                .le(endDate != null, "transaction_date", endDate)
                .orderByDesc("transaction_date");
        Page<TransactionEntity> page = transactionService.page(new Page<>(current, size), wrapper);
        return Result.ok(page);
    }

    @Operation(summary = "根据ID查询记账记录详情")
    @GetMapping("/{id}")
    public Result<TransactionEntity> getById(@PathVariable Long id) {
        return Result.ok(transactionService.getById(id));
    }

    @Operation(summary = "修改记账记录")
    @PutMapping("/{id}")
    public Result<Boolean> update(@PathVariable Long id, @RequestBody TransactionEntity entity) {
        entity.setId(id);
        return Result.ok(transactionService.updateById(entity));
    }

    @Operation(summary = "删除记账记录")
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        return Result.ok(transactionService.removeById(id));
    }
}

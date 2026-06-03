package com.hfh.api.user.controller;

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
 * 用户端-记账记录控制器（含语音记账）
 */
@Tag(name = "用户端-记账记录")
@RestController
@RequestMapping("/api/transaction")
public class TransactionController {

    @Autowired
    private ITransactionService transactionService;

    @Operation(summary = "分页查询记账记录")
    @GetMapping("/page")
    public Result<Page<TransactionEntity>> page(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam Long userId,
            @RequestParam(required = false) Long bookId,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Integer type,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        QueryWrapper<TransactionEntity> wrapper = new QueryWrapper<TransactionEntity>()
                .eq("book_id", bookId != null ? bookId : 0)
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

    @Operation(summary = "新增记账记录")
    @PostMapping
    public Result<Boolean> save(@RequestBody TransactionEntity entity) {
        return Result.ok(transactionService.save(entity));
    }

    @Operation(summary = "语音记账（传入语音识别文本）")
    @PostMapping("/voice")
    public Result<Boolean> voiceRecord(@RequestBody TransactionEntity entity) {
        // TODO: 解析 voice_text 提取金额、分类等信息后保存
        return Result.ok(transactionService.save(entity));
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

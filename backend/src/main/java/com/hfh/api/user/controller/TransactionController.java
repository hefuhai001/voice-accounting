package com.hfh.api.user.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hfh.api.common.Result;
import com.hfh.api.entity.CategoryEntity;
import com.hfh.api.entity.TransactionEntity;
import com.hfh.api.mapper.AccountBookMapper;
import com.hfh.api.mapper.CategoryMapper;
import com.hfh.api.service.ITransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 用户端-记账记录控制器（含语音记账）
 */
@Tag(name = "用户端-记账记录")
@RestController
@RequestMapping("/api/transaction")
public class TransactionController {

    @Autowired
    private ITransactionService transactionService;

    @Autowired
    private AccountBookMapper accountBookMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    @Operation(summary = "分页查询记账记录")
    @GetMapping("/page")
    public Result<Page<TransactionEntity>> page(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam Long userId,
            @RequestParam(required = false) Integer type) {
        
        // 获取用户的所有账本ID
        List<Long> bookIds = accountBookMapper.selectList(
                new QueryWrapper<com.hfh.api.entity.AccountBookEntity>()
                        .eq("user_id", userId)
                        .eq("deleted", 0)
        ).stream().map(com.hfh.api.entity.AccountBookEntity::getId).collect(Collectors.toList());
        
        if (bookIds.isEmpty()) {
            // 用户没有账本，返回空结果
            return Result.ok(new Page<>(current, size));
        }
        
        QueryWrapper<TransactionEntity> wrapper = new QueryWrapper<TransactionEntity>()
                .in("book_id", bookIds)
                .eq(type != null, "type", type)
                .orderByDesc("transaction_date");
        
        Page<TransactionEntity> page = transactionService.page(new Page<>(current, size), wrapper);

        // 填充分类名称和图标
        List<Long> categoryIds = page.getRecords().stream()
                .map(TransactionEntity::getCategoryId)
                .filter(id -> id != null)
                .distinct()
                .collect(Collectors.toList());
        if (!categoryIds.isEmpty()) {
            Map<Long, CategoryEntity> categoryMap = categoryMapper.selectBatchIds(categoryIds)
                    .stream().collect(Collectors.toMap(CategoryEntity::getId, c -> c));
            for (TransactionEntity t : page.getRecords()) {
                if (t.getCategoryId() != null) {
                    CategoryEntity cat = categoryMap.get(t.getCategoryId());
                    if (cat != null) {
                        t.setCategoryName(cat.getName());
                        t.setCategoryIcon(cat.getIcon());
                    }
                }
            }
        }

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

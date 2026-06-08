package com.hfh.api.user.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hfh.api.common.Result;
import com.hfh.api.entity.AccountBookEntity;
import com.hfh.api.entity.CategoryEntity;
import com.hfh.api.entity.ReminderEntity;
import com.hfh.api.entity.TransactionEntity;
import com.hfh.api.mapper.AccountBookMapper;
import com.hfh.api.mapper.CategoryMapper;
import com.hfh.api.mapper.ReminderMapper;
import com.hfh.api.mapper.TransactionMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 用户端-首页仪表盘控制器
 */
@Tag(name = "用户端-首页仪表盘")
@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired
    private TransactionMapper transactionMapper;

    @Autowired
    private AccountBookMapper accountBookMapper;

    @Autowired
    private ReminderMapper reminderMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    @Operation(summary = "获取首页统计数据")
    @GetMapping("/stats")
    public Result<Map<String, Object>> stats(@RequestParam Long userId) {
        // 获取用户的所有账本ID
        List<Long> bookIds = accountBookMapper.selectList(
                new QueryWrapper<AccountBookEntity>()
                        .eq("user_id", userId)
                        .eq("deleted", 0)
        ).stream().map(AccountBookEntity::getId).collect(Collectors.toList());

        Map<String, Object> result = new HashMap<>();

        if (bookIds.isEmpty()) {
            result.put("monthExpense", BigDecimal.ZERO);
            result.put("monthIncome", BigDecimal.ZERO);
            result.put("recordCount", 0);
            result.put("pendingReminders", 0);
            return Result.ok(result);
        }

        // 当月起止日期
        YearMonth currentMonth = YearMonth.now();
        LocalDate monthStart = currentMonth.atDay(1);
        LocalDate monthEnd = currentMonth.atEndOfMonth();

        // 当月支出
        QueryWrapper<TransactionEntity> expenseWrapper = new QueryWrapper<TransactionEntity>()
                .in("book_id", bookIds)
                .eq("type", 1)
                .ge("transaction_date", monthStart)
                .le("transaction_date", monthEnd);
        List<TransactionEntity> expenseList = transactionMapper.selectList(expenseWrapper);
        BigDecimal monthExpense = expenseList.stream()
                .map(TransactionEntity::getAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 当月收入
        QueryWrapper<TransactionEntity> incomeWrapper = new QueryWrapper<TransactionEntity>()
                .in("book_id", bookIds)
                .eq("type", 2)
                .ge("transaction_date", monthStart)
                .le("transaction_date", monthEnd);
        List<TransactionEntity> incomeList = transactionMapper.selectList(incomeWrapper);
        BigDecimal monthIncome = incomeList.stream()
                .map(TransactionEntity::getAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 当月记账笔数
        QueryWrapper<TransactionEntity> countWrapper = new QueryWrapper<TransactionEntity>()
                .in("book_id", bookIds)
                .ge("transaction_date", monthStart)
                .le("transaction_date", monthEnd);
        Long recordCount = transactionMapper.selectCount(countWrapper);

        // 待处理提醒数
        Long pendingReminders = reminderMapper.selectCount(
                new QueryWrapper<ReminderEntity>()
                        .eq("user_id", userId)
                        .eq("status", 0)
        );

        result.put("monthExpense", monthExpense);
        result.put("monthIncome", monthIncome);
        result.put("recordCount", recordCount);
        result.put("pendingReminders", pendingReminders);

        return Result.ok(result);
    }

    @Operation(summary = "获取最近记账记录")
    @GetMapping("/recent")
    public Result<List<Map<String, Object>>> recent(
            @RequestParam Long userId,
            @RequestParam(defaultValue = "5") Integer limit) {
        // 获取用户的所有账本ID
        List<Long> bookIds = accountBookMapper.selectList(
                new QueryWrapper<AccountBookEntity>()
                        .eq("user_id", userId)
                        .eq("deleted", 0)
        ).stream().map(AccountBookEntity::getId).collect(Collectors.toList());

        if (bookIds.isEmpty()) {
            return Result.ok(Collections.emptyList());
        }

        // 查询最近记录
        QueryWrapper<TransactionEntity> wrapper = new QueryWrapper<TransactionEntity>()
                .in("book_id", bookIds)
                .orderByDesc("transaction_date")
                .orderByDesc("created_at")
                .last("LIMIT " + limit);
        List<TransactionEntity> records = transactionMapper.selectList(wrapper);

        // 获取所有分类ID，批量查询分类名称
        Set<Long> categoryIds = records.stream()
                .map(TransactionEntity::getCategoryId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        Map<Long, CategoryEntity> categoryMap = new HashMap<>();
        if (!categoryIds.isEmpty()) {
            categoryMapper.selectBatchIds(categoryIds).forEach(c -> categoryMap.put(c.getId(), c));
        }

        // 组装结果
        List<Map<String, Object>> result = records.stream().map(r -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", r.getId());
            map.put("type", r.getType());
            map.put("amount", r.getAmount());
            map.put("remark", r.getRemark());
            map.put("transactionDate", r.getTransactionDate());
            CategoryEntity category = categoryMap.get(r.getCategoryId());
            map.put("categoryName", category != null ? category.getName() : "");
            map.put("categoryIcon", category != null ? category.getIcon() : "");
            return map;
        }).collect(Collectors.toList());

        return Result.ok(result);
    }
}

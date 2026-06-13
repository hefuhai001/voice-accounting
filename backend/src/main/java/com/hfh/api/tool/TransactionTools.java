package com.hfh.api.tool;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hfh.api.entity.AccountBookEntity;
import com.hfh.api.entity.CategoryEntity;
import com.hfh.api.entity.TransactionEntity;
import com.hfh.api.service.IAccountBookService;
import com.hfh.api.service.ICategoryService;
import com.hfh.api.service.ITransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * AI Function Calling 工具类 - 记账相关操作
 * AI通过调用这些工具完成自动记账
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TransactionTools {

    private final ITransactionService transactionService;
    private final ICategoryService categoryService;
    private final IAccountBookService accountBookService;

    @Tool(description = "查询分类列表，根据类型获取支出或收入的分类。type=1查询支出分类，type=2查询收入分类。用于记账时确定分类ID。")
    public List<CategoryEntity> listCategories(
            @ToolParam(description = "分类类型：1-支出 2-收入") Integer type) {
        log.info("AI调用工具: listCategories, type={}", type);
        return categoryService.list(
                new LambdaQueryWrapper<CategoryEntity>()
                        .eq(CategoryEntity::getType, type)
                        .isNull(CategoryEntity::getUserId)
                        .orderByAsc(CategoryEntity::getSortOrder));
    }

    @Tool(description = "获取用户的默认账本。记账时需要指定账本，如果用户没有特别说明，使用默认账本。如果用户没有默认账本会自动创建一个。")
    public AccountBookEntity getDefaultAccountBook(
            @ToolParam(description = "用户ID") Long userId) {
        log.info("AI调用工具: getDefaultAccountBook, userId={}", userId);

        // 先查已有默认账本
        AccountBookEntity book = accountBookService.getOne(
                new LambdaQueryWrapper<AccountBookEntity>()
                        .eq(AccountBookEntity::getUserId, userId)
                        .eq(AccountBookEntity::getIsDefault, 1)
                        .last("LIMIT 1"));

        if (book != null) {
            return book;
        }

        // 没有默认账本，自动创建一个
        log.info("用户{}没有默认账本，自动创建", userId);
        book = new AccountBookEntity();
        book.setUserId(userId);
        book.setName("日常账本");
        book.setType(1);
        book.setDescription("默认账本");
        book.setIsDefault(1);
        book.setIcon("book.png");
        book.setSortOrder(1);
        accountBookService.save(book);

        log.info("自动创建默认账本成功, id={}", book.getId());
        return book;
    }

    @Tool(description = "创建记账记录。将一笔收入或支出记录保存到账本中。金额始终为正数，由type区分收支。")
    public TransactionEntity createTransaction(
            @ToolParam(description = "账本ID") Long bookId,
            @ToolParam(description = "分类ID") Long categoryId,
            @ToolParam(description = "金额（正数）") BigDecimal amount,
            @ToolParam(description = "类型：1-支出 2-收入") Integer type,
            @ToolParam(description = "备注说明") String remark,
            @ToolParam(description = "交易日期，格式yyyy-MM-dd") String transactionDate,
            @ToolParam(description = "语音识别原文，如果是语音记账则传入原始文字") String voiceText) {
        log.info("AI调用工具: createTransaction, bookId={}, categoryId={}, amount={}, type={}, remark={}",
                bookId, categoryId, amount, type, remark);

        TransactionEntity entity = new TransactionEntity();
        entity.setBookId(bookId);
        entity.setCategoryId(categoryId);
        entity.setAmount(amount);
        entity.setType(type);
        entity.setRemark(remark);
        entity.setTransactionDate(LocalDate.parse(transactionDate));
        entity.setVoiceText(voiceText);

        transactionService.save(entity);
        log.info("记账成功, id={}", entity.getId());
        return entity;
    }
}

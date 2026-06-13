package com.hfh.api.service;

import com.hfh.api.tool.TransactionTools;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

/**
 * AI智能记账服务
 * 通过DeepSeek Function Calling自动分析用户记账需求并调用记账接口
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AiBookkeepingService {

    private final ChatClient chatClient;
    private final TransactionTools transactionTools;

    /**
     * 分析记账需求文本，自动调用记账工具完成记账
     *
     * @param text   用户输入的记账需求文本（如"今天午餐花了50块"）
     * @param userId 当前登录用户ID
     * @return AI的回复（包含记账结果说明）
     */
    public String analyzeAndRecord(String text, Long userId) {
        log.info("AI记账分析开始, userId={}, text={}", userId, text);

        String userMessage = String.format("""
                当前用户ID: %d
                今天日期: %s
                记账需求: %s
                """, userId, java.time.LocalDate.now(), text);

        String result = chatClient.prompt()
                .user(userMessage)
                .tools(transactionTools)
                .call()
                .content();

        log.info("AI记账分析完成, result={}", result);
        return result;
    }
}

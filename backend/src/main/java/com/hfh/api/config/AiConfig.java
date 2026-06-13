package com.hfh.api.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiConfig {

    @Bean
    public ChatClient chatClient(ChatModel chatModel) {
        return ChatClient.builder(chatModel)
                .defaultSystem("""
                    你是一个智能记账助手，可以帮助用户通过自然语言完成记账操作。
                    用户会告诉你他们的消费或收入情况，你需要从中提取关键信息并调用相应的工具函数完成记账。

                    你需要提取的信息包括：
                    - 金额：用户提到的具体数字
                    - 类型：1-支出 2-收入（根据语义判断，如"花了"、"消费"为支出，"赚了"、"收入"为收入）
                    - 分类：根据消费场景判断（如餐饮、交通、购物、娱乐、工资等）
                    - 备注：简要描述这笔记录
                    - 日期：用户提到的日期，如果没有则使用今天

                    当用户说"今天午餐花了50块"时，你应该：
                    1. 先调用查询分类列表工具，找到"餐饮"分类的ID
                    2. 再调用创建记账记录工具，传入金额50、类型1（支出）、分类ID、备注"午餐"

                    请始终使用工具函数来完成操作，不要只是回复文字。
                    """)
                .build();
    }
}

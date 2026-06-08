package com.hfh.api.config;

import org.springframework.boot.jackson.autoconfigure.JsonMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tools.jackson.databind.module.SimpleModule;
import tools.jackson.databind.ser.std.ToStringSerializer;

/**
 * Jackson 全局配置
 * 解决雪花ID（Long类型）在前端JS中精度丢失问题
 * 将所有Long类型字段序列化为字符串
 */
@Configuration
public class JacksonConfig {

    /**
     * 配置Jackson将Long类型序列化为字符串
     */
    @Bean
    public JsonMapperBuilderCustomizer jsonCustomizer() {
        return builder -> {
            SimpleModule simpleModule = new SimpleModule();
            // 将Long类型序列化为字符串
            simpleModule.addSerializer(Long.class, ToStringSerializer.instance);
            simpleModule.addSerializer(Long.TYPE, ToStringSerializer.instance);
            builder.addModule(simpleModule);
        };
    }
}

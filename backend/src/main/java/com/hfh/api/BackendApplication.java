package com.hfh.api;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@MapperScan("com.hfh.api.mapper")
@SpringBootApplication
public class BackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady(ApplicationReadyEvent event) {
        // MySQL 连通性检查
        try {
            DataSource dataSource = event.getApplicationContext().getBean(DataSource.class);
            JdbcTemplate jdbc = new JdbcTemplate(dataSource);
            jdbc.queryForObject("SELECT 1", Integer.class);
            System.out.println("[连通性检查] MySQL  yes 连接正常");
        } catch (Exception e) {
            System.err.println("[连通性检查] MySQL  no 连接失败: " + e.getMessage());
        }

        // Redis 连通性检查
        try {
            RedisConnectionFactory factory = event.getApplicationContext().getBean(RedisConnectionFactory.class);
            factory.getConnection().ping();
            System.out.println("[连通性检查] Redis  yes 连接正常");
        } catch (Exception e) {
            System.err.println("[连通性检查] Redis  no 连接失败: " + e.getMessage());
        }

        System.out.println("============================================");
        System.out.println("  应用启动成功！");
        System.out.println("  Knife4j 文档地址: http://localhost:8080/doc.html");
        System.out.println("============================================");
    }
}

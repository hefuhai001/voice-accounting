package com.hfh.api;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.sql.DataSource;

@MapperScan("com.hfh.api.mapper")
@SpringBootApplication
@EnableScheduling
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
            System.out.println("[connect test] MySQL connect normal");
        } catch (Exception e) {
            System.err.println("[connect test] MySQL connect error: " + e.getMessage());
        }

        // Redis 连通性检查
        try {
            RedisConnectionFactory factory = event.getApplicationContext().getBean(RedisConnectionFactory.class);
            factory.getConnection().ping();
            System.out.println("[connect test] Redis connect normal");
        } catch (Exception e) {
            System.err.println("[connect test] Redis connect error: " + e.getMessage());
        }

        System.out.println("============================================");
        System.out.println("  app run success！");
        System.out.println("  Knife4j doc url: http://localhost:8080/doc.html");
        System.out.println("============================================");
    }
}

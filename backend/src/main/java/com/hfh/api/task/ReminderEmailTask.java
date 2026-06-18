package com.hfh.api.task;

import com.hfh.api.service.ReminderEmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 提醒邮件定时任务
 * 负责按计划触发提醒邮件发送，本身不包含业务逻辑，
 * 仅依赖 {@link ReminderEmailService} 完成具体发送与状态流转，保持调度与业务解耦。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ReminderEmailTask {

    private final ReminderEmailService reminderEmailService;

    /**
     * 每日 20:00 执行：扫描今日到期的提醒并发送邮件
     * cron = 秒 分 时 日 月 周
     */
    @Scheduled(cron = "0 0 20 * * ?")
    public void sendDailyReminders() {
        log.info("[定时任务] 开始执行每日提醒邮件发送");
        long start = System.currentTimeMillis();
        try {
            int success = reminderEmailService.sendTodayReminders();
            long cost = System.currentTimeMillis() - start;
            log.info("[定时任务] 每日提醒邮件发送结束, success={}, cost={}ms", success, cost);
        } catch (Exception e) {
            log.error("[定时任务] 每日提醒邮件发送异常: {}", e.getMessage(), e);
        }
    }
}

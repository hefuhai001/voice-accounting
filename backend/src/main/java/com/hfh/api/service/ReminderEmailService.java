package com.hfh.api.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hfh.api.entity.ReminderEntity;
import com.hfh.api.entity.SysUserEntity;
import com.hfh.api.mapper.ReminderMapper;
import com.hfh.api.mapper.SysUserMapper;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 提醒邮件服务
 * 负责根据提醒记录向用户发送邮件通知，与提醒的业务逻辑（增删改查、状态流转）解耦。
 * 调用方只需传入提醒实体或提醒ID即可完成邮件发送，无需关心邮件构建细节。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ReminderEmailService {

    private final JavaMailSender mailSender;
    private final ReminderMapper reminderMapper;
    private final SysUserMapper sysUserMapper;

    /** 发件人地址，从邮件配置中读取，避免硬编码 */
    @Value("${spring.mail.username:noreply@voice-accounting.com}")
    private String fromEmail;

    private static final String FROM_NAME = "语音记账";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy年MM月dd日");

    /** 重复频率常量：1-一次性 2-每天 3-每周 4-每月 5-每年 */
    private static final int FREQ_ONCE = 1;
    private static final int FREQ_DAILY = 2;
    private static final int FREQ_WEEKLY = 3;
    private static final int FREQ_MONTHLY = 4;
    private static final int FREQ_YEARLY = 5;

    /**
     * 根据提醒ID发送邮件
     *
     * @param reminderId 提醒ID
     * @return 是否发送成功
     */
    public boolean sendByEmailId(Long reminderId) {
        ReminderEntity reminder = reminderMapper.selectById(reminderId);
        if (reminder == null) {
            log.warn("提醒邮件发送失败：提醒不存在, reminderId={}", reminderId);
            return false;
        }
        return send(reminder);
    }

    /**
     * 根据提醒实体发送邮件（自动查询用户邮箱）
     *
     * @param reminder 提醒实体
     * @return 是否发送成功
     */
    public boolean send(ReminderEntity reminder) {
        if (reminder == null || reminder.getUserId() == null) {
            log.warn("提醒邮件发送失败：提醒或用户ID为空");
            return false;
        }
        SysUserEntity user = sysUserMapper.selectById(reminder.getUserId());
        if (user == null) {
            log.warn("提醒邮件发送失败：用户不存在, userId={}", reminder.getUserId());
            return false;
        }
        return send(reminder, user);
    }

    /**
     * 发送提醒邮件（核心方法，需调用方保证 user 与 reminder 对应）
     *
     * @param reminder 提醒实体
     * @param user     用户实体
     * @return 是否发送成功
     */
    public boolean send(ReminderEntity reminder, SysUserEntity user) {
        if (reminder == null || user == null) {
            log.warn("提醒邮件发送失败：参数为空");
            return false;
        }
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            log.warn("提醒邮件发送失败：用户邮箱为空, userId={}, reminderId={}",
                    user.getId(), reminder.getId());
            return false;
        }

        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            helper.setFrom(fromEmail, FROM_NAME);
            helper.setTo(user.getEmail());
            helper.setSubject(buildSubject(reminder));
            helper.setText(buildContent(reminder, user), true);
            mailSender.send(mimeMessage);
            log.info("提醒邮件发送成功: reminderId={}, email={}", reminder.getId(), user.getEmail());
            return true;
        } catch (MessagingException | UnsupportedEncodingException e) {
            log.error("提醒邮件发送失败: reminderId={}, email={}, error={}",
                    reminder.getId(), user.getEmail(), e.getMessage());
            return false;
        }
    }

    /**
     * 批量发送今日到期且待提醒的邮件
     * 可由定时任务调用，实现每日提醒推送。
     * 发送成功后会根据重复频率更新提醒状态：
     * - 一次性提醒：标记为已提醒（status=1）
     * - 重复提醒：更新 remindDate 为下一次日期，并标记 isRepeated=1
     *
     * @return 成功发送的邮件数量
     */
    public int sendTodayReminders() {
        LocalDate today = LocalDate.now();
        List<ReminderEntity> reminders = reminderMapper.selectList(
                new LambdaQueryWrapper<ReminderEntity>()
                        .eq(ReminderEntity::getRemindDate, today)
                        .eq(ReminderEntity::getStatus, 0)
        );
        if (reminders.isEmpty()) {
            log.info("今日无到期提醒: date={}", today);
            return 0;
        }

        int successCount = 0;
        for (ReminderEntity reminder : reminders) {
            if (send(reminder)) {
                successCount++;
                updateReminderAfterSent(reminder, today);
            }
        }
        log.info("今日提醒邮件发送完成: total={}, success={}, date={}",
                reminders.size(), successCount, today);
        return successCount;
    }

    /**
     * 邮件发送成功后更新提醒状态
     * - 一次性提醒：标记为已提醒
     * - 重复提醒：计算下一次提醒日期并更新
     *
     * @param reminder 提醒实体
     * @param today    当前日期
     */
    private void updateReminderAfterSent(ReminderEntity reminder, LocalDate today) {
        try {
            Integer frequency = reminder.getFrequency();
            // 一次性提醒或频率为空：标记为已提醒
            if (frequency == null || frequency == FREQ_ONCE) {
                ReminderEntity update = new ReminderEntity();
                update.setId(reminder.getId());
                update.setStatus(1);
                reminderMapper.updateById(update);
                return;
            }

            // 重复提醒：计算下一次提醒日期
            LocalDate nextDate = calculateNextRemindDate(reminder.getRemindDate(), frequency, today);
            ReminderEntity update = new ReminderEntity();
            update.setId(reminder.getId());
            update.setRemindDate(nextDate);
            update.setIsRepeated(1);
            reminderMapper.updateById(update);
            log.info("重复提醒已更新下次日期: reminderId={}, nextDate={}", reminder.getId(), nextDate);
        } catch (Exception e) {
            log.error("更新提醒状态失败: reminderId={}, error={}", reminder.getId(), e.getMessage());
        }
    }

    /**
     * 根据重复频率计算下一次提醒日期
     * 以当前提醒日期为基准累加一个周期，若结果已过去则以今天为基准累加
     *
     * @param currentRemindDate 当前提醒日期
     * @param frequency        重复频率
     * @param today             今天
     * @return 下一次提醒日期
     */
    private LocalDate calculateNextRemindDate(LocalDate currentRemindDate, Integer frequency, LocalDate today) {
        LocalDate base = currentRemindDate != null ? currentRemindDate : today;
        LocalDate next;
        switch (frequency) {
            case FREQ_DAILY -> next = base.plusDays(1);
            case FREQ_WEEKLY -> next = base.plusWeeks(1);
            case FREQ_MONTHLY -> next = base.plusMonths(1);
            case FREQ_YEARLY -> next = base.plusYears(1);
            default -> next = base.plusDays(1);
        }
        // 若计算出的下次日期仍早于今天（如服务停机多日），则从今天起重新计算
        while (next.isBefore(today)) {
            switch (frequency) {
                case FREQ_DAILY -> next = next.plusDays(1);
                case FREQ_WEEKLY -> next = next.plusWeeks(1);
                case FREQ_MONTHLY -> next = next.plusMonths(1);
                case FREQ_YEARLY -> next = next.plusYears(1);
                default -> next = next.plusDays(1);
            }
        }
        return next;
    }

    /**
     * 构建邮件主题
     */
    private String buildSubject(ReminderEntity reminder) {
        String title = reminder.getTitle() != null ? reminder.getTitle() : "您有一条新提醒";
        return "【语音记账】提醒：" + title;
    }

    /**
     * 构建HTML邮件内容
     */
    private String buildContent(ReminderEntity reminder, SysUserEntity user) {
        String nickname = (user.getNickname() != null && !user.getNickname().isBlank())
                ? user.getNickname()
                : (user.getUsername() != null ? user.getUsername() : "用户");
        String title = reminder.getTitle() != null ? reminder.getTitle() : "—";
        String amount = reminder.getAmount() != null
                ? "¥" + reminder.getAmount().toPlainString() : "—";
        String dateStr = reminder.getRemindDate() != null
                ? reminder.getRemindDate().format(DATE_FORMATTER) : "—";
        String frequency = frequencyText(reminder.getFrequency());
        String remark = (reminder.getRemark() != null && !reminder.getRemark().isBlank())
                ? reminder.getRemark() : "无";

        return """
                <div style="padding:24px;font-family:system-ui,-apple-system,sans-serif;background:#f7f7f9;">
                  <div style="max-width:560px;margin:0 auto;background:#fff;border-radius:12px;overflow:hidden;box-shadow:0 2px 8px rgba(0,0,0,0.05);">
                    <div style="padding:20px 24px;background:#ff6b35;color:#fff;">
                      <h2 style="margin:0;font-size:18px;">语音记账 · 提醒通知</h2>
                    </div>
                    <div style="padding:24px;">
                      <p style="margin:0 0 16px;font-size:15px;color:#333;">您好，<strong>%s</strong>：</p>
                      <p style="margin:0 0 16px;font-size:15px;color:#333;">您设置的一条提醒已到期，详情如下：</p>
                      <table style="width:100%%;border-collapse:collapse;font-size:14px;color:#333;">
                        <tr><td style="padding:8px 0;color:#999;width:90px;">提醒标题</td><td style="padding:8px 0;">%s</td></tr>
                        <tr><td style="padding:8px 0;color:#999;">提醒金额</td><td style="padding:8px 0;color:#ff6b35;font-weight:bold;">%s</td></tr>
                        <tr><td style="padding:8px 0;color:#999;">提醒日期</td><td style="padding:8px 0;">%s</td></tr>
                        <tr><td style="padding:8px 0;color:#999;">重复频率</td><td style="padding:8px 0;">%s</td></tr>
                        <tr><td style="padding:8px 0;color:#999;">备注</td><td style="padding:8px 0;">%s</td></tr>
                      </table>
                      <p style="margin:16px 0 0;font-size:12px;color:#999;">本邮件由系统自动发送，请勿直接回复。</p>
                    </div>
                  </div>
                </div>
                """.formatted(nickname, title, amount, dateStr, frequency, remark);
    }

    /**
     * 频率文案转换
     */
    private String frequencyText(Integer frequency) {
        if (frequency == null) {
            return "—";
        }
        return switch (frequency) {
            case 1 -> "一次性";
            case 2 -> "每天";
            case 3 -> "每周";
            case 4 -> "每月";
            case 5 -> "每年";
            default -> "—";
        };
    }
}

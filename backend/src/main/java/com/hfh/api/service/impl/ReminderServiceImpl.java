package com.hfh.api.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hfh.api.entity.ReminderEntity;
import com.hfh.api.mapper.ReminderMapper;
import com.hfh.api.service.IReminderService;
import org.springframework.stereotype.Service;

/**
 * 提醒服务实现
 */
@Service
public class ReminderServiceImpl extends ServiceImpl<ReminderMapper, ReminderEntity> implements IReminderService {
}

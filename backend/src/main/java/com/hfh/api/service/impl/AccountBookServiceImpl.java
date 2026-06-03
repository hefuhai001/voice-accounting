package com.hfh.api.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hfh.api.entity.AccountBookEntity;
import com.hfh.api.mapper.AccountBookMapper;
import com.hfh.api.service.IAccountBookService;
import org.springframework.stereotype.Service;

/**
 * 账本服务实现
 */
@Service
public class AccountBookServiceImpl extends ServiceImpl<AccountBookMapper, AccountBookEntity> implements IAccountBookService {
}

package com.hfh.api.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hfh.api.entity.TransactionEntity;
import com.hfh.api.mapper.TransactionMapper;
import com.hfh.api.service.ITransactionService;
import org.springframework.stereotype.Service;

/**
 * 记账记录服务实现
 */
@Service
public class TransactionServiceImpl extends ServiceImpl<TransactionMapper, TransactionEntity> implements ITransactionService {
}

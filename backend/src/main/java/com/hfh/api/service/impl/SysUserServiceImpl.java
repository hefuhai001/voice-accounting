package com.hfh.api.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hfh.api.entity.SysUserEntity;
import com.hfh.api.mapper.SysUserMapper;
import com.hfh.api.service.ISysUserService;
import org.springframework.stereotype.Service;

/**
 * 用户服务实现
 */
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUserEntity> implements ISysUserService {
}

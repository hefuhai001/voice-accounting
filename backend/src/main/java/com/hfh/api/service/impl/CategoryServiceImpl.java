package com.hfh.api.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hfh.api.entity.CategoryEntity;
import com.hfh.api.mapper.CategoryMapper;
import com.hfh.api.service.ICategoryService;
import org.springframework.stereotype.Service;

/**
 * 分类服务实现
 */
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, CategoryEntity> implements ICategoryService {
}

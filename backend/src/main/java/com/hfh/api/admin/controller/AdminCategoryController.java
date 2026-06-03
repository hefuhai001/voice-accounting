package com.hfh.api.admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hfh.api.common.Result;
import com.hfh.api.entity.CategoryEntity;
import com.hfh.api.service.ICategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 管理端-分类管理控制器（管理系统默认分类）
 */
@Tag(name = "管理端-分类管理")
@RestController
@RequestMapping("/admin/category")
public class AdminCategoryController {

    @Autowired
    private ICategoryService categoryService;

    @Operation(summary = "分页查询所有分类")
    @GetMapping("/page")
    public Result<Page<CategoryEntity>> page(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            CategoryEntity query) {
        Page<CategoryEntity> page = categoryService.page(new Page<>(current, size),
                new QueryWrapper<CategoryEntity>()
                        .eq(query.getType() != null, "type", query.getType())
                        .like(query.getName() != null, "name", query.getName())
                        .orderByAsc("type", "sort_order"));
        return Result.ok(page);
    }

    @Operation(summary = "根据ID查询分类详情")
    @GetMapping("/{id}")
    public Result<CategoryEntity> getById(@PathVariable Long id) {
        return Result.ok(categoryService.getById(id));
    }

    @Operation(summary = "新增系统分类")
    @PostMapping
    public Result<Boolean> save(@RequestBody CategoryEntity entity) {
        // 管理端新增的分类设为系统默认（userId=null）
        entity.setUserId(null);
        return Result.ok(categoryService.save(entity));
    }

    @Operation(summary = "修改分类")
    @PutMapping("/{id}")
    public Result<Boolean> update(@PathVariable Long id, @RequestBody CategoryEntity entity) {
        entity.setId(id);
        return Result.ok(categoryService.updateById(entity));
    }

    @Operation(summary = "删除分类")
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        return Result.ok(categoryService.removeById(id));
    }
}

package com.hfh.api.user.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hfh.api.common.Result;
import com.hfh.api.entity.CategoryEntity;
import com.hfh.api.service.ICategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户端-分类查询控制器（只读，查看可用分类）
 */
@Tag(name = "用户端-分类查询")
@RestController
@RequestMapping("/api/category")
public class UserCategoryController {

    @Autowired
    private ICategoryService categoryService;

    @Operation(summary = "获取支出分类列表（系统默认+自定义）")
    @GetMapping("/expense")
    public Result<List<CategoryEntity>> expenseList(@RequestParam(required = false) Long userId) {
        QueryWrapper<CategoryEntity> wrapper = new QueryWrapper<CategoryEntity>()
                .eq("type", 1)
                .and(w -> w.isNull("user_id").or().eq("user_id", userId))
                .orderByAsc("sort_order");
        return Result.ok(categoryService.list(wrapper));
    }

    @Operation(summary = "获取收入分类列表（系统默认+自定义）")
    @GetMapping("/income")
    public Result<List<CategoryEntity>> incomeList(@RequestParam(required = false) Long userId) {
        QueryWrapper<CategoryEntity> wrapper = new QueryWrapper<CategoryEntity>()
                .eq("type", 2)
                .and(w -> w.isNull("user_id").or().eq("user_id", userId))
                .orderByAsc("sort_order");
        return Result.ok(categoryService.list(wrapper));
    }

    @Operation(summary = "获取全部分类列表")
    @GetMapping("/list")
    public Result<List<CategoryEntity>> list(@RequestParam(required = false) Long userId) {
        QueryWrapper<CategoryEntity> wrapper = new QueryWrapper<CategoryEntity>()
                .and(w -> w.isNull("user_id").or().eq("user_id", userId))
                .orderByAsc("type", "sort_order");
        return Result.ok(categoryService.list(wrapper));
    }

    @Operation(summary = "根据ID查询分类详情")
    @GetMapping("/{id}")
    public Result<CategoryEntity> getById(@PathVariable Long id) {
        return Result.ok(categoryService.getById(id));
    }

    @Operation(summary = "添加自定义分类")
    @PostMapping
    public Result<Boolean> save(@RequestBody CategoryEntity entity) {
        return Result.ok(categoryService.save(entity));
    }

    @Operation(summary = "修改自定义分类")
    @PutMapping("/{id}")
    public Result<Boolean> update(@PathVariable Long id, @RequestBody CategoryEntity entity) {
        entity.setId(id);
        return Result.ok(categoryService.updateById(entity));
    }

    @Operation(summary = "删除自定义分类")
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        return Result.ok(categoryService.removeById(id));
    }
}

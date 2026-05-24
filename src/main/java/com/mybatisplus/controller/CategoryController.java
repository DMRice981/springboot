package com.mybatisplus.controller;

import com.mybatisplus.common.Result;
import com.mybatisplus.entity.Category;
import com.mybatisplus.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/category")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping("/list")
    public Result<List<Category>> list() {
        return Result.success(categoryService.lambdaQuery()
                .eq(Category::getIsDelete, 0)
                .orderByAsc(Category::getSort)
                .list());
    }

    @GetMapping("/get/{id}")
    public Result<Category> get(@PathVariable Integer id) {
        Category category = categoryService.getById(id);
        if (category == null) {
            return Result.error("分类不存在");
        }
        return Result.success(category);
    }

    @PostMapping("/add")
    public Result<Category> add(@RequestBody Category category) {
        categoryService.save(category);
        return Result.success("添加成功", category);
    }

    @PutMapping("/update")
    public Result<Category> update(@RequestBody Category category) {
        categoryService.updateById(category);
        return Result.success("更新成功", category);
    }

    @DeleteMapping("/delete/{id}")
    public Result<Void> delete(@PathVariable Integer id) {
        categoryService.lambdaUpdate()
                .eq(Category::getId, id)
                .set(Category::getIsDelete, 1)
                .update();
        return Result.success();
    }
}

package com.mybatisplus.controller;

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
    public List<Category> list(){
        return categoryService.list();
    }

    @GetMapping("/get/{id}")
    public Category get(@PathVariable Integer id){
        return categoryService.getById(id);
    }

    @PostMapping("/add")
    public boolean add(@RequestBody Category category){
        return categoryService.save(category);
    }

    @PutMapping("/update")
    public boolean update(@RequestBody Category category){
        return categoryService.updateById(category);
    }

    @DeleteMapping("/delete/{id}")
    public boolean delete(@PathVariable Integer id){
        return categoryService.removeById(id);
    }
}
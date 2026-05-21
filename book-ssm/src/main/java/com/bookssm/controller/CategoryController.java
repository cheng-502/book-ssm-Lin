package com.bookssm.controller;

import com.bookssm.dto.Result;
import com.bookssm.entity.Category;
import com.bookssm.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 图书分类管理控制器
 */
@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping
    public Result<List<Category>> list() {
        return Result.success(categoryService.list());
    }

    @PostMapping("/create")
    public Result<Category> create(
            @RequestParam("name") String name,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "sortOrder", required = false) Integer sortOrder) {
        return Result.success("新增分类成功", categoryService.create(name, description, sortOrder));
    }

    @PostMapping("/update")
    public Result<Category> update(
            @RequestParam("id") Long id,
            @RequestParam("name") String name,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "sortOrder", required = false) Integer sortOrder) {
        return Result.success("修改分类成功", categoryService.update(id, name, description, sortOrder));
    }

    @PostMapping("/delete")
    public Result<?> delete(@RequestParam("id") Long id) {
        categoryService.delete(id);
        return Result.success("删除分类成功", null);
    }
}

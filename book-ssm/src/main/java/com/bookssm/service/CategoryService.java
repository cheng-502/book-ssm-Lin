package com.bookssm.service;

import com.bookssm.entity.Category;

import java.util.List;

/**
 * 图书分类服务接口
 */
public interface CategoryService {

    Category create(String name, String description, Integer sortOrder);

    Category update(Long id, String name, String description, Integer sortOrder);

    void delete(Long id);

    List<Category> list();

    Category findById(Long id);
}

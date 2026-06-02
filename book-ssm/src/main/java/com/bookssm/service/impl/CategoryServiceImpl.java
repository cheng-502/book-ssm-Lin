package com.bookssm.service.impl;

import com.bookssm.entity.Category;
import com.bookssm.exception.BusinessException;
import com.bookssm.mapper.CategoryMapper;
import com.bookssm.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 图书分类服务实现
 */
@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    @Transactional
    public Category create(String name, String description, Integer sortOrder) {
        Category category = new Category();
        category.setName(requireText(name, "分类名称不能为空"));
        category.setDescription(trimToNull(description));
        category.setSortOrder(sortOrder == null ? 0 : sortOrder);

        try {
            categoryMapper.insert(category);
        } catch (DuplicateKeyException e) {
            throw new BusinessException(400, "分类名称已存在");
        }
        return categoryMapper.findById(category.getId());
    }

    @Override
    @Transactional
    public Category update(Long id, String name, String description, Integer sortOrder) {
        Category exists = findRequired(id);

        Category sameName = categoryMapper.findByName(requireText(name, "分类名称不能为空"));
        if (sameName != null && !sameName.getId().equals(exists.getId())) {
            throw new BusinessException(400, "分类名称已存在");
        }

        exists.setName(name.trim());
        exists.setDescription(trimToNull(description));
        exists.setSortOrder(sortOrder == null ? 0 : sortOrder);
        categoryMapper.update(exists);
        return categoryMapper.findById(id);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        findRequired(id);
        if (categoryMapper.countBooks(id) > 0) {
            throw new BusinessException(400, "分类下存在图书，不能删除");
        }
        categoryMapper.deleteById(id);
    }

    @Override
    public List<Category> list() {
        return categoryMapper.findAll();
    }

    @Override
    public Category findById(Long id) {
        return categoryMapper.findById(id);
    }

    private Category findRequired(Long id) {
        if (id == null) {
            throw new BusinessException(400, "分类ID不能为空");
        }
        Category category = categoryMapper.findById(id);
        if (category == null) {
            throw new BusinessException(404, "分类不存在");
        }
        return category;
    }

    private String requireText(String value, String message) {
        if (value == null || value.trim().isEmpty()) {
            throw new BusinessException(400, message);
        }
        return value.trim();
    }

    private String trimToNull(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        return value.trim();
    }
}

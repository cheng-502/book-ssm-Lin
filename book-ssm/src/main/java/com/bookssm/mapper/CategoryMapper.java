package com.bookssm.mapper;

import com.bookssm.entity.Category;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 图书分类 Mapper 接口
 */
public interface CategoryMapper {

    Category findById(@Param("id") Long id);

    Category findByName(@Param("name") String name);

    List<Category> findAll();

    int countBooks(@Param("id") Long id);

    int insert(Category category);

    int update(Category category);

    int deleteById(@Param("id") Long id);
}

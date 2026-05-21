package com.bookssm.mapper;

import com.bookssm.entity.Book;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 图书 Mapper 接口
 */
public interface BookMapper {

    Book findById(@Param("id") Long id);

    Book findByIsbn(@Param("isbn") String isbn);

    long countByCondition(@Param("title") String title,
                          @Param("author") String author,
                          @Param("publisher") String publisher,
                          @Param("categoryId") Long categoryId,
                          @Param("status") Integer status);

    List<Book> findByCondition(@Param("title") String title,
                               @Param("author") String author,
                               @Param("publisher") String publisher,
                               @Param("categoryId") Long categoryId,
                               @Param("status") Integer status,
                               @Param("offset") int offset,
                               @Param("pageSize") int pageSize);

    int insert(Book book);

    int update(Book book);

    int deleteById(@Param("id") Long id);

    int updateStatus(@Param("id") Long id, @Param("status") Integer status);
}

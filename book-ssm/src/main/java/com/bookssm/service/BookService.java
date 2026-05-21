package com.bookssm.service;

import com.bookssm.dto.PageResult;
import com.bookssm.entity.Book;

import java.math.BigDecimal;

/**
 * 图书服务接口
 */
public interface BookService {

    Book create(String isbn, String title, String author, String publisher, Long categoryId,
                BigDecimal price, BigDecimal costPrice, Integer status, String description);

    Book update(Long id, String isbn, String title, String author, String publisher, Long categoryId,
                BigDecimal price, BigDecimal costPrice, Integer status, String description);

    void delete(Long id);

    PageResult<Book> list(String title, String author, String publisher, Long categoryId,
                          Integer status, Integer page, Integer pageSize);

    Book onSale(Long id);

    Book offSale(Long id);

    Book findById(Long id);
}

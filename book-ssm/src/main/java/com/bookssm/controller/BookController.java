package com.bookssm.controller;

import com.bookssm.dto.PageResult;
import com.bookssm.dto.Result;
import com.bookssm.entity.Book;
import com.bookssm.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

/**
 * 图书管理控制器
 */
@RestController
@RequestMapping("/api/books")
public class BookController {

    @Autowired
    private BookService bookService;

    @GetMapping
    public Result<PageResult<Book>> list(
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "author", required = false) String author,
            @RequestParam(value = "publisher", required = false) String publisher,
            @RequestParam(value = "categoryId", required = false) Long categoryId,
            @RequestParam(value = "status", required = false) Integer status,
            @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
            @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize) {
        return Result.success(bookService.list(title, author, publisher, categoryId, status, page, pageSize));
    }

    @PostMapping("/create")
    public Result<Book> create(
            @RequestParam("isbn") String isbn,
            @RequestParam("title") String title,
            @RequestParam("author") String author,
            @RequestParam("publisher") String publisher,
            @RequestParam("categoryId") Long categoryId,
            @RequestParam("price") BigDecimal price,
            @RequestParam(value = "costPrice", required = false) BigDecimal costPrice,
            @RequestParam(value = "status", required = false) Integer status,
            @RequestParam(value = "description", required = false) String description) {
        return Result.success("新增图书成功", bookService.create(isbn, title, author, publisher, categoryId,
                price, costPrice, status, description));
    }

    @PostMapping("/update")
    public Result<Book> update(
            @RequestParam("id") Long id,
            @RequestParam("isbn") String isbn,
            @RequestParam("title") String title,
            @RequestParam("author") String author,
            @RequestParam("publisher") String publisher,
            @RequestParam("categoryId") Long categoryId,
            @RequestParam("price") BigDecimal price,
            @RequestParam(value = "costPrice", required = false) BigDecimal costPrice,
            @RequestParam(value = "status", required = false) Integer status,
            @RequestParam(value = "description", required = false) String description) {
        return Result.success("修改图书成功", bookService.update(id, isbn, title, author, publisher, categoryId,
                price, costPrice, status, description));
    }

    @PostMapping("/delete")
    public Result<?> delete(@RequestParam("id") Long id) {
        bookService.delete(id);
        return Result.success("删除图书成功", null);
    }

    @PostMapping("/on-sale")
    public Result<Book> onSale(@RequestParam("id") Long id) {
        return Result.success("图书上架成功", bookService.onSale(id));
    }

    @PostMapping("/off-sale")
    public Result<Book> offSale(@RequestParam("id") Long id) {
        return Result.success("图书下架成功", bookService.offSale(id));
    }
}

package com.bookssm.service.impl;

import com.bookssm.dto.PageResult;
import com.bookssm.entity.Book;
import com.bookssm.entity.Category;
import com.bookssm.exception.BusinessException;
import com.bookssm.mapper.BookMapper;
import com.bookssm.mapper.CategoryMapper;
import com.bookssm.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * 图书服务实现
 */
@Service
public class BookServiceImpl implements BookService {

    @Autowired
    private BookMapper bookMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    @Transactional
    public Book create(String isbn, String title, String author, String publisher, Long categoryId,
                       BigDecimal price, BigDecimal costPrice, Integer status, String description) {
        Book book = buildBook(null, isbn, title, author, publisher, categoryId, price, costPrice, status, description);
        ensureUnique(book, null);
        try {
            bookMapper.insert(book);
        } catch (DuplicateKeyException e) {
            throw new BusinessException(400, "ISBN或图书信息已存在");
        }
        return bookMapper.findById(book.getId());
    }

    @Override
    @Transactional
    public Book update(Long id, String isbn, String title, String author, String publisher, Long categoryId,
                       BigDecimal price, BigDecimal costPrice, Integer status, String description) {
        findRequired(id);
        Book book = buildBook(id, isbn, title, author, publisher, categoryId, price, costPrice, status, description);
        ensureUnique(book, id);
        try {
            bookMapper.update(book);
        } catch (DuplicateKeyException e) {
            throw new BusinessException(400, "ISBN或图书信息已存在");
        }
        return bookMapper.findById(id);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        findRequired(id);
        bookMapper.deleteById(id);
    }

    @Override
    public PageResult<Book> list(String title, String author, String publisher, Long categoryId,
                                 Integer status, Integer page, Integer pageSize) {
        int currentPage = page == null || page < 1 ? 1 : page;
        int currentPageSize = pageSize == null || pageSize < 1 ? 10 : pageSize;
        if (currentPageSize > 100) {
            currentPageSize = 100;
        }
        int offset = (currentPage - 1) * currentPageSize;

        String titleLike = trimToNull(title);
        String authorLike = trimToNull(author);
        String publisherLike = trimToNull(publisher);

        long total = bookMapper.countByCondition(titleLike, authorLike, publisherLike, categoryId, status);
        List<Book> list = bookMapper.findByCondition(titleLike, authorLike, publisherLike, categoryId,
                status, offset, currentPageSize);
        return new PageResult<>(total, currentPage, currentPageSize, list);
    }

    @Override
    @Transactional
    public Book onSale(Long id) {
        findRequired(id);
        bookMapper.updateStatus(id, 1);
        return bookMapper.findById(id);
    }

    @Override
    @Transactional
    public Book offSale(Long id) {
        findRequired(id);
        bookMapper.updateStatus(id, 0);
        return bookMapper.findById(id);
    }

    @Override
    public Book findById(Long id) {
        return bookMapper.findById(id);
    }

    private Book buildBook(Long id, String isbn, String title, String author, String publisher, Long categoryId,
                           BigDecimal price, BigDecimal costPrice, Integer status, String description) {
        Category category = findCategoryRequired(categoryId);
        Book book = new Book();
        book.setId(id);
        book.setIsbn(requireText(isbn, "ISBN不能为空"));
        book.setTitle(requireText(title, "书名不能为空"));
        book.setAuthor(requireText(author, "作者不能为空"));
        book.setPublisher(requireText(publisher, "出版社不能为空"));
        book.setCategoryId(category.getId());
        book.setPrice(requirePositive(price, "售价必须大于0"));
        book.setCostPrice(costPrice == null ? book.getPrice() : requirePositive(costPrice, "成本价必须大于0"));
        book.setStatus(status == null ? 1 : normalizeStatus(status));
        book.setDescription(trimToNull(description));
        book.setStock(0);
        return book;
    }

    private void ensureUnique(Book book, Long selfId) {
        Book sameIsbn = bookMapper.findByIsbn(book.getIsbn());
        if (sameIsbn != null && !sameIsbn.getId().equals(selfId)) {
            throw new BusinessException(400, "ISBN已存在");
        }

    }

    private Book findRequired(Long id) {
        if (id == null) {
            throw new BusinessException(400, "图书ID不能为空");
        }
        Book book = bookMapper.findById(id);
        if (book == null) {
            throw new BusinessException(404, "图书不存在");
        }
        return book;
    }

    private Category findCategoryRequired(Long categoryId) {
        if (categoryId == null) {
            throw new BusinessException(400, "分类ID不能为空");
        }
        Category category = categoryMapper.findById(categoryId);
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

    private BigDecimal requirePositive(BigDecimal value, String message) {
        if (value == null || value.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException(400, message);
        }
        return value;
    }

    private Integer normalizeStatus(Integer status) {
        if (status != 0 && status != 1) {
            throw new BusinessException(400, "状态只能是0或1");
        }
        return status;
    }

    private String trimToNull(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        return value.trim();
    }
}

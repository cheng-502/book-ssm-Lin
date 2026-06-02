package com.bookssm.service.impl;

import com.bookssm.entity.Book;
import com.bookssm.entity.CartItem;
import com.bookssm.exception.BusinessException;
import com.bookssm.mapper.BookMapper;
import com.bookssm.mapper.CartMapper;
import com.bookssm.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 购物车服务实现
 */
@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private CartMapper cartMapper;

    @Autowired
    private BookMapper bookMapper;

    @Override
    @Transactional
    public CartItem add(Long userId, Long bookId, Integer quantity) {
        requireUser(userId);
        if (bookId == null) {
            throw new BusinessException(400, "图书ID不能为空");
        }
        int addQuantity = requireQuantity(quantity);

        Book book = bookMapper.findById(bookId);
        if (book == null) {
            throw new BusinessException(404, "图书不存在");
        }
        if (book.getStatus() == null || book.getStatus() != 1) {
            throw new BusinessException(400, "图书已下架，不能加入购物车");
        }

        CartItem exists = cartMapper.findByUserAndBook(userId, bookId);
        if (exists != null) {
            int newQuantity = exists.getQuantity() + addQuantity;
            cartMapper.updateQuantity(exists.getId(), userId, newQuantity);
            return cartMapper.findByIdAndUser(exists.getId(), userId);
        }

        CartItem cartItem = new CartItem();
        cartItem.setUserId(userId);
        cartItem.setBookId(bookId);
        cartItem.setQuantity(addQuantity);
        cartMapper.insert(cartItem);
        return cartMapper.findByIdAndUser(cartItem.getId(), userId);
    }

    @Override
    @Transactional
    public CartItem updateQuantity(Long userId, Long id, Integer quantity) {
        requireUser(userId);
        if (id == null) {
            throw new BusinessException(400, "购物车项ID不能为空");
        }
        int newQuantity = requireQuantity(quantity);
        CartItem cartItem = cartMapper.findByIdAndUser(id, userId);
        if (cartItem == null) {
            throw new BusinessException(404, "购物车项不存在");
        }
        cartMapper.updateQuantity(id, userId, newQuantity);
        return cartMapper.findByIdAndUser(id, userId);
    }

    @Override
    @Transactional
    public void delete(Long userId, Long id) {
        requireUser(userId);
        if (id == null) {
            throw new BusinessException(400, "购物车项ID不能为空");
        }
        CartItem cartItem = cartMapper.findByIdAndUser(id, userId);
        if (cartItem == null) {
            throw new BusinessException(404, "购物车项不存在");
        }
        cartMapper.deleteByIdAndUser(id, userId);
    }

    @Override
    public List<CartItem> list(Long userId) {
        requireUser(userId);
        return cartMapper.findByUserId(userId);
    }

    private void requireUser(Long userId) {
        if (userId == null) {
            throw new BusinessException(401, "请先登录");
        }
    }

    private int requireQuantity(Integer quantity) {
        if (quantity == null || quantity <= 0) {
            throw new BusinessException(400, "数量必须大于0");
        }
        return quantity;
    }
}

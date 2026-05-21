package com.bookssm.service;

import com.bookssm.entity.CartItem;

import java.util.List;

/**
 * 购物车服务接口
 */
public interface CartService {

    CartItem add(Long userId, Long bookId, Integer quantity);

    CartItem updateQuantity(Long userId, Long id, Integer quantity);

    void delete(Long userId, Long id);

    List<CartItem> list(Long userId);
}

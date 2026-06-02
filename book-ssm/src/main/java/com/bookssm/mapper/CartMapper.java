package com.bookssm.mapper;

import com.bookssm.entity.CartItem;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 购物车 Mapper 接口
 */
public interface CartMapper {

    CartItem findByUserAndBook(@Param("userId") Long userId, @Param("bookId") Long bookId);

    CartItem findByIdAndUser(@Param("id") Long id, @Param("userId") Long userId);

    List<CartItem> findByUserId(@Param("userId") Long userId);

    List<CartItem> findByIdsAndUser(@Param("ids") List<Long> ids, @Param("userId") Long userId);

    int insert(CartItem cartItem);

    int updateQuantity(@Param("id") Long id,
                       @Param("userId") Long userId,
                       @Param("quantity") Integer quantity);

    int deleteByIdAndUser(@Param("id") Long id, @Param("userId") Long userId);

    int deleteByIdsAndUser(@Param("ids") List<Long> ids, @Param("userId") Long userId);
}

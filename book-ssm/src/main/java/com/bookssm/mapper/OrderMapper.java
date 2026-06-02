package com.bookssm.mapper;

import com.bookssm.entity.Order;
import com.bookssm.entity.OrderItem;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 订单 Mapper 接口
 */
public interface OrderMapper {

    int insertOrder(Order order);

    int insertOrderItem(OrderItem item);

    Order findByIdAndUser(@Param("id") Long id, @Param("userId") Long userId);

    Order findById(@Param("id") Long id);

    Order findByIdForUpdate(@Param("id") Long id);

    long countByUser(@Param("userId") Long userId,
                     @Param("status") String status);

    List<Order> findByUser(@Param("userId") Long userId,
                           @Param("status") String status,
                           @Param("offset") int offset,
                           @Param("pageSize") int pageSize);

    long countAll(@Param("status") String status,
                  @Param("username") String username);

    List<Order> findAll(@Param("status") String status,
                        @Param("username") String username,
                        @Param("offset") int offset,
                        @Param("pageSize") int pageSize);

    List<OrderItem> findItemsByOrderId(@Param("orderId") Long orderId);

    int confirmOrder(@Param("id") Long id);

    int shipOrder(@Param("id") Long id);
}

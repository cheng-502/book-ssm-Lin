package com.bookssm.service;

import com.bookssm.dto.PageResult;
import com.bookssm.entity.Order;

import java.util.List;

/**
 * 订单服务接口
 */
public interface OrderService {

    Order createFromCart(Long userId, List<Long> cartItemIds, String receiverName,
                         String receiverPhone, String receiverAddress, String remark);

    PageResult<Order> listMine(Long userId, String status, Integer page, Integer pageSize);

    Order findMine(Long userId, Long orderId);
}

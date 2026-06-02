package com.bookssm.service;

import com.bookssm.dto.PageResult;
import com.bookssm.entity.Order;
import com.bookssm.entity.User;

/**
 * 管理员订单服务接口
 */
public interface AdminOrderService {

    PageResult<Order> list(User operator, String status, String username, Integer page, Integer pageSize);

    Order confirm(User operator, Long id);

    Order ship(User operator, Long id);
}

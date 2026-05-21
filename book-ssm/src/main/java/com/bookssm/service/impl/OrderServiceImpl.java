package com.bookssm.service.impl;

import com.bookssm.dto.PageResult;
import com.bookssm.entity.CartItem;
import com.bookssm.entity.Order;
import com.bookssm.entity.OrderItem;
import com.bookssm.exception.BusinessException;
import com.bookssm.mapper.CartMapper;
import com.bookssm.mapper.OrderMapper;
import com.bookssm.service.OrderService;
import com.bookssm.util.OrderNoUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * 订单服务实现
 */
@Service
public class OrderServiceImpl implements OrderService {

    private static final int MAX_ORDER_NO_RETRY = 5;

    @Autowired
    private CartMapper cartMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Override
    @Transactional
    public Order createFromCart(Long userId, List<Long> cartItemIds, String receiverName,
                                String receiverPhone, String receiverAddress, String remark) {
        requireUser(userId);
        List<Long> ids = normalizeIds(cartItemIds);
        List<CartItem> cartItems = cartMapper.findByIdsAndUser(ids, userId);
        if (cartItems.size() != ids.size()) {
            throw new BusinessException(400, "存在无效的购物车项");
        }

        BigDecimal totalAmount = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItem cartItem : cartItems) {
            validateCartItemForOrder(cartItem);
            BigDecimal subtotal = cartItem.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity()));
            totalAmount = totalAmount.add(subtotal);

            OrderItem orderItem = new OrderItem();
            orderItem.setBookId(cartItem.getBookId());
            orderItem.setBookTitle(cartItem.getBookTitle());
            orderItem.setBookPrice(cartItem.getPrice());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setSubtotal(subtotal);
            orderItems.add(orderItem);
        }

        Order order = new Order();
        order.setUserId(userId);
        order.setTotalAmount(totalAmount);
        order.setStatus("PENDING");
        order.setReceiverName(trimToNull(receiverName));
        order.setReceiverPhone(trimToNull(receiverPhone));
        order.setReceiverAddress(trimToNull(receiverAddress));
        order.setRemark(trimToNull(remark));
        insertOrderWithRetry(order);

        for (OrderItem item : orderItems) {
            item.setOrderId(order.getId());
            orderMapper.insertOrderItem(item);
        }

        cartMapper.deleteByIdsAndUser(ids, userId);
        return fillItems(orderMapper.findByIdAndUser(order.getId(), userId));
    }

    @Override
    public PageResult<Order> listMine(Long userId, String status, Integer page, Integer pageSize) {
        requireUser(userId);
        int currentPage = page == null || page < 1 ? 1 : page;
        int currentPageSize = pageSize == null || pageSize < 1 ? 10 : Math.min(pageSize, 100);
        int offset = (currentPage - 1) * currentPageSize;
        String orderStatus = trimToNull(status);

        long total = orderMapper.countByUser(userId, orderStatus);
        List<Order> orders = orderMapper.findByUser(userId, orderStatus, offset, currentPageSize);
        for (Order order : orders) {
            order.setItems(orderMapper.findItemsByOrderId(order.getId()));
        }
        return new PageResult<>(total, currentPage, currentPageSize, orders);
    }

    @Override
    public Order findMine(Long userId, Long orderId) {
        requireUser(userId);
        if (orderId == null) {
            throw new BusinessException(400, "订单ID不能为空");
        }
        Order order = orderMapper.findByIdAndUser(orderId, userId);
        if (order == null) {
            throw new BusinessException(404, "订单不存在");
        }
        return fillItems(order);
    }

    private void validateCartItemForOrder(CartItem cartItem) {
        if (cartItem.getBookTitle() == null) {
            throw new BusinessException(404, "图书不存在");
        }
        if (cartItem.getStatus() == null || cartItem.getStatus() != 1) {
            throw new BusinessException(400, "图书已下架：" + cartItem.getBookTitle());
        }
        if (cartItem.getStock() == null || cartItem.getStock() < cartItem.getQuantity()) {
            throw new BusinessException(400, "库存不足：" + cartItem.getBookTitle());
        }
        if (cartItem.getQuantity() == null || cartItem.getQuantity() <= 0) {
            throw new BusinessException(400, "购物车数量无效：" + cartItem.getBookTitle());
        }
    }

    private void insertOrderWithRetry(Order order) {
        for (int i = 0; i < MAX_ORDER_NO_RETRY; i++) {
            order.setOrderNo(OrderNoUtil.generate());
            try {
                orderMapper.insertOrder(order);
                return;
            } catch (DuplicateKeyException e) {
                if (i == MAX_ORDER_NO_RETRY - 1) {
                    throw new BusinessException(500, "生成订单号失败，请重试");
                }
            }
        }
    }

    private List<Long> normalizeIds(List<Long> cartItemIds) {
        if (cartItemIds == null || cartItemIds.isEmpty()) {
            throw new BusinessException(400, "请选择要下单的购物车项");
        }
        Set<Long> idSet = new LinkedHashSet<>();
        for (Long id : cartItemIds) {
            if (id == null || id <= 0) {
                throw new BusinessException(400, "购物车项ID无效");
            }
            idSet.add(id);
        }
        return new ArrayList<>(idSet);
    }

    private Order fillItems(Order order) {
        if (order != null) {
            order.setItems(orderMapper.findItemsByOrderId(order.getId()));
        }
        return order;
    }

    private void requireUser(Long userId) {
        if (userId == null) {
            throw new BusinessException(401, "请先登录");
        }
    }

    private String trimToNull(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        return value.trim();
    }
}

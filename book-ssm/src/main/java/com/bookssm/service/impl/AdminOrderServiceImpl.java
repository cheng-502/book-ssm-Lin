package com.bookssm.service.impl;

import com.bookssm.dto.PageResult;
import com.bookssm.entity.Order;
import com.bookssm.entity.OrderItem;
import com.bookssm.entity.StockBatch;
import com.bookssm.entity.StockRecord;
import com.bookssm.entity.User;
import com.bookssm.exception.BusinessException;
import com.bookssm.mapper.OrderMapper;
import com.bookssm.mapper.StockMapper;
import com.bookssm.service.AdminOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 管理员订单服务实现
 */
@Service
public class AdminOrderServiceImpl implements AdminOrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private StockMapper stockMapper;

    @Override
    public PageResult<Order> list(User operator, String status, String username, Integer page, Integer pageSize) {
        requireAdmin(operator);
        int currentPage = page == null || page < 1 ? 1 : page;
        int currentPageSize = pageSize == null || pageSize < 1 ? 10 : Math.min(pageSize, 100);
        int offset = (currentPage - 1) * currentPageSize;
        String orderStatus = trimToNull(status);
        String usernameLike = trimToNull(username);

        long total = orderMapper.countAll(orderStatus, usernameLike);
        List<Order> orders = orderMapper.findAll(orderStatus, usernameLike, offset, currentPageSize);
        for (Order order : orders) {
            order.setItems(orderMapper.findItemsByOrderId(order.getId()));
        }
        return new PageResult<>(total, currentPage, currentPageSize, orders);
    }

    @Override
    @Transactional
    public Order confirm(User operator, Long id) {
        requireAdmin(operator);
        Order order = findOrderRequired(id);
        if (!"PENDING".equals(order.getStatus())) {
            throw new BusinessException(400, "订单状态不允许确认");
        }
        if (orderMapper.confirmOrder(id) != 1) {
            throw new BusinessException(400, "订单状态不允许确认");
        }
        return fillItems(orderMapper.findById(id));
    }

    @Override
    @Transactional
    public Order ship(User operator, Long id) {
        requireAdmin(operator);
        Order order = findOrderRequiredForUpdate(id);
        if (!"CONFIRMED".equals(order.getStatus())) {
            throw new BusinessException(400, "订单状态不允许发货");
        }

        List<OrderItem> items = orderMapper.findItemsByOrderId(id);
        if (items == null || items.isEmpty()) {
            throw new BusinessException(400, "订单明细为空");
        }

        for (OrderItem item : items) {
            shipOrderItem(order, item, operator.getId());
        }

        if (orderMapper.shipOrder(id) != 1) {
            throw new BusinessException(400, "订单状态不允许发货");
        }
        return fillItems(orderMapper.findById(id));
    }

    private void shipOrderItem(Order order, OrderItem item, Long operatorId) {
        Integer bookStock = stockMapper.findBookStockForUpdate(item.getBookId());
        if (bookStock == null) {
            throw new BusinessException(404, "图书不存在：" + item.getBookTitle());
        }
        if (bookStock < item.getQuantity()) {
            throw new BusinessException(400, "图书库存不足：" + item.getBookTitle());
        }

        int needQuantity = item.getQuantity();
        List<StockBatch> batches = stockMapper.findAvailableBatchesForUpdate(item.getBookId());
        if (batches == null || batches.isEmpty()) {
            throw new BusinessException(400, "库存批次不存在：" + item.getBookTitle());
        }

        for (StockBatch batch : batches) {
            if (needQuantity <= 0) {
                break;
            }
            int beforeRemain = batch.getRemainQuantity() == null ? 0 : batch.getRemainQuantity();
            if (beforeRemain <= 0) {
                continue;
            }
            int deductQuantity = Math.min(beforeRemain, needQuantity);
            int afterRemain = beforeRemain - deductQuantity;
            if (stockMapper.updateBatchRemain(batch.getId(), beforeRemain, afterRemain) != 1) {
                throw new BusinessException(500, "批次库存扣减失败：" + batch.getBatchNo());
            }

            StockRecord record = new StockRecord();
            record.setBookId(item.getBookId());
            record.setBatchId(batch.getId());
            record.setType("OUT");
            record.setQuantity(-deductQuantity);
            record.setBeforeStock(beforeRemain);
            record.setAfterStock(afterRemain);
            record.setOrderId(order.getId());
            record.setOperatorId(operatorId);
            record.setRemark("订单发货出库：" + order.getOrderNo());
            stockMapper.insertRecord(record);

            needQuantity -= deductQuantity;
        }

        if (needQuantity > 0) {
            throw new BusinessException(400, "图书库存不足：" + item.getBookTitle());
        }

        stockMapper.updateBookStock(item.getBookId(), bookStock - item.getQuantity());
    }

    private Order findOrderRequired(Long id) {
        if (id == null) {
            throw new BusinessException(400, "订单ID不能为空");
        }
        Order order = orderMapper.findById(id);
        if (order == null) {
            throw new BusinessException(404, "订单不存在");
        }
        return order;
    }

    private Order findOrderRequiredForUpdate(Long id) {
        if (id == null) {
            throw new BusinessException(400, "订单ID不能为空");
        }
        Order order = orderMapper.findByIdForUpdate(id);
        if (order == null) {
            throw new BusinessException(404, "订单不存在");
        }
        return order;
    }

    private Order fillItems(Order order) {
        if (order != null) {
            order.setItems(orderMapper.findItemsByOrderId(order.getId()));
        }
        return order;
    }

    private void requireAdmin(User operator) {
        if (operator == null) {
            throw new BusinessException(401, "请先登录");
        }
        if (!"ADMIN".equals(operator.getRole())) {
            throw new BusinessException(403, "非管理员用户不能访问管理员接口");
        }
    }

    private String trimToNull(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        return value.trim();
    }
}

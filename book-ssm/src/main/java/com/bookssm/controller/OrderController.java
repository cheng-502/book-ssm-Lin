package com.bookssm.controller;

import com.bookssm.dto.PageResult;
import com.bookssm.dto.Result;
import com.bookssm.entity.Order;
import com.bookssm.exception.BusinessException;
import com.bookssm.service.OrderService;
import com.bookssm.util.SessionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

/**
 * 订单控制器
 */
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/create")
    public Result<Order> create(
            @RequestParam("cartItemIds") String cartItemIds,
            @RequestParam(value = "receiverName", required = false) String receiverName,
            @RequestParam(value = "receiverPhone", required = false) String receiverPhone,
            @RequestParam(value = "receiverAddress", required = false) String receiverAddress,
            @RequestParam(value = "remark", required = false) String remark,
            HttpSession session) {
        Long userId = SessionUtil.getLoginUserId(session);
        return Result.success("创建订单成功", orderService.createFromCart(userId, parseIds(cartItemIds),
                receiverName, receiverPhone, receiverAddress, remark));
    }

    @GetMapping("/mine")
    public Result<PageResult<Order>> mine(
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
            @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize,
            HttpSession session) {
        Long userId = SessionUtil.getLoginUserId(session);
        return Result.success(orderService.listMine(userId, status, page, pageSize));
    }

    @GetMapping("/{id}")
    public Result<Order> detail(@PathVariable("id") Long id, HttpSession session) {
        Long userId = SessionUtil.getLoginUserId(session);
        return Result.success(orderService.findMine(userId, id));
    }

    private List<Long> parseIds(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new BusinessException(400, "请选择要下单的购物车项");
        }
        String[] parts = value.split(",");
        List<Long> ids = new ArrayList<>();
        for (String part : parts) {
            String text = part.trim();
            if (text.isEmpty()) {
                continue;
            }
            try {
                ids.add(Long.valueOf(text));
            } catch (NumberFormatException e) {
                throw new BusinessException(400, "购物车项ID格式错误");
            }
        }
        return ids;
    }
}

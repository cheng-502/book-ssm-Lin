package com.bookssm.controller;

import com.bookssm.dto.PageResult;
import com.bookssm.dto.Result;
import com.bookssm.entity.Order;
import com.bookssm.entity.User;
import com.bookssm.service.AdminOrderService;
import com.bookssm.util.SessionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

/**
 * 管理员订单控制器
 */
@RestController
@RequestMapping("/api/admin/orders")
public class AdminOrderController {

    @Autowired
    private AdminOrderService adminOrderService;

    @GetMapping
    public Result<PageResult<Order>> list(
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "username", required = false) String username,
            @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
            @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize,
            HttpSession session) {
        User operator = SessionUtil.getLoginUser(session);
        return Result.success(adminOrderService.list(operator, status, username, page, pageSize));
    }

    @PutMapping("/{id}/confirm")
    public Result<Order> confirm(@PathVariable("id") Long id, HttpSession session) {
        User operator = SessionUtil.getLoginUser(session);
        return Result.success("确认订单成功", adminOrderService.confirm(operator, id));
    }

    @PutMapping("/{id}/ship")
    public Result<Order> ship(@PathVariable("id") Long id, HttpSession session) {
        User operator = SessionUtil.getLoginUser(session);
        return Result.success("订单发货成功", adminOrderService.ship(operator, id));
    }
}

package com.bookssm.controller;

import com.bookssm.dto.Result;
import com.bookssm.entity.CartItem;
import com.bookssm.service.CartService;
import com.bookssm.util.SessionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * 购物车控制器
 */
@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @GetMapping
    public Result<List<CartItem>> list(HttpSession session) {
        Long userId = SessionUtil.getLoginUserId(session);
        return Result.success(cartService.list(userId));
    }

    @PostMapping("/add")
    public Result<CartItem> add(
            @RequestParam("bookId") Long bookId,
            @RequestParam(value = "quantity", required = false, defaultValue = "1") Integer quantity,
            HttpSession session) {
        Long userId = SessionUtil.getLoginUserId(session);
        return Result.success("添加购物车成功", cartService.add(userId, bookId, quantity));
    }

    @PostMapping("/update")
    public Result<CartItem> update(
            @RequestParam("id") Long id,
            @RequestParam("quantity") Integer quantity,
            HttpSession session) {
        Long userId = SessionUtil.getLoginUserId(session);
        return Result.success("修改购物车数量成功", cartService.updateQuantity(userId, id, quantity));
    }

    @PostMapping("/delete")
    public Result<?> delete(@RequestParam("id") Long id, HttpSession session) {
        Long userId = SessionUtil.getLoginUserId(session);
        cartService.delete(userId, id);
        return Result.success("删除购物车商品成功", null);
    }
}

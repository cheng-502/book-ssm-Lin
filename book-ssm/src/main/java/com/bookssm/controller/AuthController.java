package com.bookssm.controller;

import com.bookssm.dto.Result;
import com.bookssm.entity.User;
import com.bookssm.service.UserService;
import com.bookssm.util.SessionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

/**
 * 认证控制器
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public Result<Map<String, Object>> register(
            @RequestParam("username") String username,
            @RequestParam("password") String password,
            @RequestParam(value = "nickname", required = false, defaultValue = "") String nickname) {
        User user = userService.register(username, password, nickname);
        Map<String, Object> data = new HashMap<>();
        data.put("id", user.getId());
        data.put("username", user.getUsername());
        data.put("nickname", user.getNickname());
        data.put("role", user.getRole());
        return Result.success("注册成功", data);
    }

    @PostMapping("/login")
    public Result<Map<String, Object>> login(
            @RequestParam("username") String username,
            @RequestParam("password") String password,
            HttpSession session) {
        User user = userService.login(username, password);
        SessionUtil.setLoginUser(session, user);
        Map<String, Object> data = new HashMap<>();
        data.put("id", user.getId());
        data.put("username", user.getUsername());
        data.put("nickname", user.getNickname());
        data.put("role", user.getRole());
        return Result.success("登录成功", data);
    }

    @PostMapping("/logout")
    public Result<?> logout(HttpSession session) {
        SessionUtil.removeLoginUser(session);
        return Result.success("已退出登录");
    }

    @GetMapping("/current")
    public Result<Map<String, Object>> current(HttpSession session) {
        User user = SessionUtil.getLoginUser(session);
        if (user == null) {
            return Result.error(401, "未登录");
        }
        Map<String, Object> data = new HashMap<>();
        data.put("id", user.getId());
        data.put("username", user.getUsername());
        data.put("nickname", user.getNickname());
        data.put("role", user.getRole());
        data.put("phone", user.getPhone());
        data.put("email", user.getEmail());
        data.put("status", user.getStatus());
        return Result.success(data);
    }
}

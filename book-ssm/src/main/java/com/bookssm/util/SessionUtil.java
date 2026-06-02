package com.bookssm.util;

import com.bookssm.entity.User;

import javax.servlet.http.HttpSession;

/**
 * 登录用户上下文工具类
 */
public class SessionUtil {

    private static final String LOGIN_USER_KEY = "loginUser";

    /**
     * 将登录用户存入 Session
     */
    public static void setLoginUser(HttpSession session, User user) {
        session.setAttribute(LOGIN_USER_KEY, user);
    }

    /**
     * 从 Session 获取当前登录用户
     */
    public static User getLoginUser(HttpSession session) {
        return (User) session.getAttribute(LOGIN_USER_KEY);
    }

    /**
     * 获取当前登录用户ID（不存在时返回 null）
     */
    public static Long getLoginUserId(HttpSession session) {
        User user = getLoginUser(session);
        return user != null ? user.getId() : null;
    }

    /**
     * 判断是否已登录
     */
    public static boolean isLogin(HttpSession session) {
        return getLoginUser(session) != null;
    }

    /**
     * 清除登录状态
     */
    public static void removeLoginUser(HttpSession session) {
        session.removeAttribute(LOGIN_USER_KEY);
    }
}

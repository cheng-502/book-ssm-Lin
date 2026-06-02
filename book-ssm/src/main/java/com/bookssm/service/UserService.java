package com.bookssm.service;

import com.bookssm.entity.User;

/**
 * 用户服务接口
 */
public interface UserService {

    /**
     * 用户注册
     */
    User register(String username, String password, String nickname);

    /**
     * 用户登录，返回用户信息
     */
    User login(String username, String password);

    /**
     * 根据ID查询用户
     */
    User findById(Long id);
}

package com.bookssm.service.impl;

import com.bookssm.entity.User;
import com.bookssm.exception.BusinessException;
import com.bookssm.mapper.UserMapper;
import com.bookssm.service.UserService;
import com.bookssm.util.MD5Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 用户服务实现
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public User register(String username, String password, String nickname) {
        if (username == null || username.trim().isEmpty()) {
            throw new BusinessException(400, "用户名不能为空");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new BusinessException(400, "密码不能为空");
        }

        User existUser = userMapper.findByUsername(username.trim());
        if (existUser != null) {
            throw new BusinessException(400, "用户名已存在");
        }

        User user = new User();
        user.setUsername(username.trim());
        user.setPassword(MD5Util.md5(password));
        user.setNickname(nickname != null ? nickname.trim() : username.trim());
        user.setRole("USER");
        user.setStatus(1);

        userMapper.insert(user);
        return user;
    }

    @Override
    public User login(String username, String password) {
        if (username == null || username.trim().isEmpty()) {
            throw new BusinessException(400, "用户名不能为空");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new BusinessException(400, "密码不能为空");
        }

        User user = userMapper.findByUsername(username.trim());
        if (user == null) {
            throw new BusinessException(400, "用户名或密码错误");
        }
        if (user.getStatus() == null || user.getStatus() != 1) {
            throw new BusinessException(403, "账号已被禁用");
        }
        if (!MD5Util.md5(password).equals(user.getPassword())) {
            throw new BusinessException(400, "用户名或密码错误");
        }

        return user;
    }

    @Override
    public User findById(Long id) {
        return userMapper.findById(id);
    }
}

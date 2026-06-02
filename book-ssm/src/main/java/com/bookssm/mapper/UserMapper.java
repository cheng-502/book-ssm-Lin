package com.bookssm.mapper;

import com.bookssm.entity.User;
import org.apache.ibatis.annotations.Param;

/**
 * 用户 Mapper 接口
 */
public interface UserMapper {

    User findByUsername(@Param("username") String username);

    User findById(@Param("id") Long id);

    int insert(User user);
}

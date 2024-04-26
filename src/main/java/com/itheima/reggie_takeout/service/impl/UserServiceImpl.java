package com.itheima.reggie_takeout.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie_takeout.entity.User;
import com.itheima.reggie_takeout.mapper.UserMapper;
import com.itheima.reggie_takeout.service.UserService;
import org.springframework.stereotype.Service;

/**
 * @Author zou
 * @Date 2024/4/15
 * @Update 2024/4/15
 * @Description
 */

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
}

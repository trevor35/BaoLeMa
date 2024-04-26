package com.itheima.reggie_takeout.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie_takeout.entity.ShoppingCart;
import com.itheima.reggie_takeout.mapper.ShoppingCartMapper;
import com.itheima.reggie_takeout.service.ShoppingCartService;
import org.springframework.stereotype.Service;

/**
 * @Author zou
 * @Date 2024/4/16
 * @Update 2024/4/16
 * @Description
 */

@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements ShoppingCartService {
}

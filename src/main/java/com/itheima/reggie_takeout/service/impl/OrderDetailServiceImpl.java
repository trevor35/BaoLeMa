package com.itheima.reggie_takeout.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie_takeout.entity.OrderDetail;
import com.itheima.reggie_takeout.mapper.OrderDetailMapper;
import com.itheima.reggie_takeout.service.OrderDetailService;
import org.springframework.stereotype.Service;

/**
 * @Author zou
 * @Date 2024/4/24
 * @Update 2024/4/24
 * @Description
 */

@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail> implements OrderDetailService {
}

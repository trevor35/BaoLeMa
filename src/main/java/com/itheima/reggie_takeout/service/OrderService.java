package com.itheima.reggie_takeout.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie_takeout.entity.Orders;

public interface OrderService extends IService<Orders> {

    // 用户下单 封装到service
    public void submit(Orders orders);
}

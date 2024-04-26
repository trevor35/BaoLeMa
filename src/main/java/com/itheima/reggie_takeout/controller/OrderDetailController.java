package com.itheima.reggie_takeout.controller;

import com.itheima.reggie_takeout.service.OrderDetailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author zou
 * @Date 2024/4/24
 * @Update 2024/4/24
 * @Description
 */

@Slf4j
@RestController
@RequestMapping("/orderDetail")
public class OrderDetailController {


    @Autowired
    private OrderDetailService orderDetailService;
}

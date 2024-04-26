package com.itheima.reggie_takeout.controller;

import com.itheima.reggie_takeout.common.R;
import com.itheima.reggie_takeout.entity.Orders;
import com.itheima.reggie_takeout.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    /**
     * 用户下单
     * @param orders
     * @return
     */
    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders){
        log.info("用户下单，订单数据{}",orders);
        orderService.submit(orders);
        return R.success("下单成功");
    }

}

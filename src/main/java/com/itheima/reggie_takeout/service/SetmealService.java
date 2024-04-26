package com.itheima.reggie_takeout.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie_takeout.dto.SetmealDto;
import com.itheima.reggie_takeout.entity.Dish;
import com.itheima.reggie_takeout.entity.Setmeal;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {

    // 新增套餐，同时需要保存套餐和菜品的关联关系
    public void saveWithDish(SetmealDto setmealDto);

    // 删除套餐 同时需删除套餐与菜品的关联数据 操作两张表 setmeal\setmealDish
    public void removeWithSetmealDish(List<Long> ids);
}

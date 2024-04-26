package com.itheima.reggie_takeout.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie_takeout.dto.DishDto;
import com.itheima.reggie_takeout.entity.Dish;

import java.util.List;

public interface DishService extends IService<Dish> {
    // 新增菜品，同时插入菜品对应的口味数据，需要操作两张表：dish、dish_flavor
    public void saveWithFlavor(DishDto dishDto);

    // 根据ID查询菜品信息和对应的口味信息, 需要操作两张表：dish、dish_flavor
    public DishDto getByIdWithFlavor (Long id);

    //更新菜品信息，同时更新对应的口味信息
    public void updateWithFlavor(DishDto dishDto);


    public boolean updateStatusByIds(int status, List<Long> ids);

//    public boolean deleteByIds(List<Long> ids);
}

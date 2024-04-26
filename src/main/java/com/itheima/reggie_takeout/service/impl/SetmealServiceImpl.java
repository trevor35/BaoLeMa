package com.itheima.reggie_takeout.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie_takeout.common.CustomException;
import com.itheima.reggie_takeout.dto.SetmealDto;
import com.itheima.reggie_takeout.entity.Dish;
import com.itheima.reggie_takeout.entity.Setmeal;
import com.itheima.reggie_takeout.entity.SetmealDish;
import com.itheima.reggie_takeout.mapper.SetmealMapper;
import com.itheima.reggie_takeout.service.SetmealDishService;
import com.itheima.reggie_takeout.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author zou
 * @Date 2024/4/8
 * @Update 2024/4/8
 * @Description
 */

@Slf4j
@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {

    @Autowired
    private SetmealDishService setmealDishService;

    // 新增套餐，同时需要保存套餐和菜品的关联关系
    @Transactional
    @Override
    public void saveWithDish(SetmealDto setmealDto) {
        // 保存套餐的基本信息，操作setmeal表，执行insert操作
        this.save(setmealDto);

        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes.stream().map((item) -> {
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());

        // 保存套餐和菜品的关联信息，操作setmeal_dish，执行insert操作
        setmealDishService.saveBatch(setmealDishes);
    }

    //删除套餐 同时需删除套餐与菜品的关联数据 操作两张表 setmeal\setmealDish
    @Override
    @Transactional
    public void removeWithSetmealDish(List<Long> ids) {
        // select count(*) from setmeal where id in (1,2,3) and status;
        // 查询套餐状态，确定是否可以删除
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Setmeal::getId,ids);
        queryWrapper.eq(Setmeal::getStatus,1);
        int count = (int) this.count(queryWrapper);

        //套餐正在售卖中，则抛出一个业务异常
        if (count>0){
            //若不能删除，抛出一个业务异常
            throw new CustomException("套餐正在售卖中，无法删除");
        }
        // 若可以删除，先删除套餐表中的数据--setmeal
        this.removeByIds(ids);

        // 再删除关系表中的数据--setmeal_dish
        // delete from setmeal_dish where setmeal_id in (1,2,3)
        LambdaQueryWrapper<SetmealDish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(SetmealDish::getSetmealId,ids);

        setmealDishService.remove(lambdaQueryWrapper);
    }
}

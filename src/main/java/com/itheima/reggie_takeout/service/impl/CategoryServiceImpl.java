package com.itheima.reggie_takeout.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie_takeout.common.CustomException;
import com.itheima.reggie_takeout.entity.Category;
import com.itheima.reggie_takeout.entity.Dish;
import com.itheima.reggie_takeout.entity.Setmeal;
import com.itheima.reggie_takeout.mapper.CategoryMapper;
import com.itheima.reggie_takeout.service.CategoryService;
import com.itheima.reggie_takeout.service.DishService;
import com.itheima.reggie_takeout.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Autowired
    private DishService dishService;

    @Autowired
    private SetmealService setmealService;

    /**
     * 根据ID删除分类，删除之前需要进行判断
     * @param ids
     */
    @Override
    public void remove(Long ids) {
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        //添加查询条件，根据分类id进行查询
        dishLambdaQueryWrapper.eq(Dish::getCategoryId,ids);
        int count1 = (int) dishService.count(dishLambdaQueryWrapper);

        //查询当前菜品分类是否关联了菜品，如有关联，则抛出一个业务异常
        if (count1>0){
            //有关联，抛出一个业务异常
            throw new CustomException("当前菜品分类已关联菜品，无法删除");
        }

        //========================

        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        //添加查询条件，根据分类id进行查询
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId,ids);
        int count2 = (int) setmealService.count(setmealLambdaQueryWrapper);

        //查询当前套餐分类是否关联了菜品，如有关联，则抛出一个业务异常
        if (count2>0){
            //有关联，抛出一个业务异常
            throw new CustomException("当前套餐分类已关联菜品，无法删除");
        }

        //正常删除
        super.removeById(ids);
    }
}

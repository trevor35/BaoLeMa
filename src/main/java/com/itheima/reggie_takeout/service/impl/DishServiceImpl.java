package com.itheima.reggie_takeout.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie_takeout.dto.DishDto;
import com.itheima.reggie_takeout.entity.Dish;
import com.itheima.reggie_takeout.entity.DishFlavor;
import com.itheima.reggie_takeout.mapper.DishMapper;
import com.itheima.reggie_takeout.service.DishFlavorrService;
import com.itheima.reggie_takeout.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
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
public class DishServiceImpl extends ServiceImpl<DishMapper,Dish> implements DishService {

    @Autowired
    private DishFlavorrService dishFlavorrService;
    /**
     * 新增菜品，同时保存对应的口味数据
     * @param dishDto = dish + dish_flavor
     */
    @Transactional
    public void saveWithFlavor(DishDto dishDto) {
        // 保存菜品的基本信息到菜品表dish
        this.save(dishDto);

        Long dishId = dishDto.getId(); //菜品Id
        // 菜品口味
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors = flavors.stream().map((item) -> {
            item.setDishId(dishId);
            return item;
        }).collect(Collectors.toList());


        // 保存菜品口味数据到口味表dish_flavor
        dishFlavorrService.saveBatch(flavors);

    }

    /**
     * 根据ID查询菜品信息和对应的口味信息, 需要操作两张表：dish、dish_flavor
     * @param id
     * @return
     */
    @Override
    public DishDto getByIdWithFlavor(Long id) {
        //查询菜品基本信息 dish表
        Dish dish = this.getById(id);

        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(dish,dishDto);

        //查询当前菜品对应的口味信息 dish_flavor表
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,dish.getId());
        List<DishFlavor> flavors = dishFlavorrService.list(queryWrapper);
        dishDto.setFlavors(flavors);

        return dishDto;
    }

    /**
     * 更新菜品信息，同时更新对应的口味信息
     * @param dishDto
     */
    @Override
    @Transactional
    public void updateWithFlavor(DishDto dishDto) {
        //更新菜品表基本信息
        this.updateById(dishDto);

        //清理当前菜品对应口味数据 dish_flavor表的delete操作
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,dishDto.getId());
        dishFlavorrService.remove(queryWrapper);

        //添加当前提交过来的口味数据 dish_flavor表的insert操作
        List<DishFlavor> flavors = dishDto.getFlavors();

        flavors = flavors.stream().map((item) -> {
            item.setDishId(dishDto.getId());
            return item;
        }).collect(Collectors.toList());

        dishFlavorrService.saveBatch(flavors);
    }

    /**
     * 批量更新菜品状态
     * @param status 要更新的状态
     * @param ids 菜品ID列表
     * @return 更新操作是否成功
     */
    @Override
    public boolean updateStatusByIds(int status, List<Long> ids) {

        List<Dish> dishes = ids.stream().map(id -> {
            Dish dish = new Dish();
            dish.setId(id);
            dish.setStatus(status);
            return dish;
        }).collect(Collectors.toList());

        // 使用 MyBatis Plus 的批量更新方法
        return this.updateBatchById(dishes);
    }


}

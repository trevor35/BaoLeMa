package com.itheima.reggie_takeout.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie_takeout.common.R;
import com.itheima.reggie_takeout.dto.DishDto;
import com.itheima.reggie_takeout.entity.Category;
import com.itheima.reggie_takeout.entity.Dish;
import com.itheima.reggie_takeout.entity.DishFlavor;
import com.itheima.reggie_takeout.service.CategoryService;
import com.itheima.reggie_takeout.service.DishFlavorrService;
import com.itheima.reggie_takeout.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @Author zou
 * @Date 2024/4/9
 * @Update 2024/4/9
 * @Description 菜品管理
 */

@Slf4j
@RestController
@RequestMapping("/dish")
public class DishController {

    @Autowired
    private DishService dishService;

    @Autowired
    private DishFlavorrService dishFlavorService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 新增菜品 DishDto = Dish + DishFlavor
     * @param dishDto
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto){
        log.info(dishDto.toString());
        dishService.saveWithFlavor(dishDto);

        //1 清理所有菜品的缓存数据
        // Set keys = redisTemplate.keys("dish_*");
        // redisTemplate.delete(keys);

        //2 清理某个分类下面的菜品缓存数据
        String key = "dish_" + dishDto.getCategoryId() + "_1";
        redisTemplate.delete(key);

        return R.success("新增菜品成功");
    }

    /**
     * 菜品信息 分页查询
     * @param page 当前页码
     * @param pageSize 每页显示的记录数
     * @param name 菜品名称，用于模糊查询
     * @return 返回分页查询的结果，包含当前页的菜品信息
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name){
        // 构造分页构造器对象，用于实现分页逻辑
        Page<Dish> pageInfo = new Page<>(page,pageSize);

        // 新建一个DishDto分页对象，用于存储转换后的数据
        Page<DishDto> dishDtoPage = new Page<>();

        // 条件构造器，用于构建查询条件
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        // 添加过滤条件，进行模糊查询。当name不为null时，根据菜品名称进行模糊查询
        queryWrapper.like(name!=null,Dish::getName,name);
        // 添加排序条件，按照更新时间降序排序
        queryWrapper.orderByDesc(Dish::getUpdateTime);
        // 执行分页查询，根据条件分页查询菜品信息
        dishService.page(pageInfo,queryWrapper);

        // 响应的数据中只有categoryId，没有分类名称categoryName。需要进行数据转换
        // 对象拷贝，将pageInfo中的数据复制到dishDtoPage中，除了records字段
        BeanUtils.copyProperties(pageInfo, dishDtoPage,"records");

        // 获取查询到的菜品记录
        List<Dish> records = pageInfo.getRecords();
        // 将Dish对象转换为DishDto对象，并设置每个DishDto的categoryName
        List<DishDto> list = records.stream().map((item) -> {
            DishDto dishDto = new DishDto();

            // 对象属性拷贝
            BeanUtils.copyProperties(item,dishDto);

            // 获取菜品的分类Id
            Long categoryId = item.getCategoryId();
            // 根据分类Id查询分类对象
            Category category = categoryService.getById(categoryId);
            if (category != null){
                // 获取分类名称
                String categoryName = category.getName();
                // 设置菜品的分类名称
                dishDto.setCategoryName(categoryName);
            }
            return dishDto;
        }).collect(Collectors.toList());

        // 设置转换后的数据集合到分页对象中
        dishDtoPage.setRecords(list);

        // 返回包装后的分页查询结果
        return R.success(dishDtoPage);
    }


    /**
     * 根据ID查询菜品信息和对应的口味信息,主要逻辑封装再service当中
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<DishDto> get(@PathVariable Long id){
        DishDto dishDto = dishService.getByIdWithFlavor(id);
        return R.success(dishDto);
    }

    /**
     * 更新菜品 DishDto = Dish + DishFlavor
     * @param dishDto
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto){
        log.info(dishDto.toString());
        dishService.updateWithFlavor(dishDto);

        //1 清理所有菜品的缓存数据
        // Set keys = redisTemplate.keys("dish_*");
        // redisTemplate.delete(keys);

        //2 清理某个分类下面的菜品缓存数据
        String key = "dish_" + dishDto.getCategoryId() + "_1";
        redisTemplate.delete(key);

        return R.success("更新菜品成功");
    }


    /**
     * 菜品状态更新，可批量
     * @param status
     * @param ids
     * @return
     */
    @PostMapping("/status/{status}")
    public R<String> updateStatus(@PathVariable int status, @RequestParam List<Long> ids) {
        // 批量更新菜品状态
        boolean success = dishService.updateStatusByIds(status, ids);
        if (success) {
            log.info("菜品状态更新成功：{}，IDs：{}", status == 0 ? "停售" : "启售", ids);
            return R.success(status == 0 ? "菜品停售成功" : "菜品启售成功");
        } else {
            log.info("菜品状态更新失败，IDs：{}", ids);
            return R.error("菜品状态更新失败");
        }
    }


    // 批量删除功能待实现

    /**
     * 删除菜品
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> delete(@RequestParam Long ids){
        log.info("ids={}菜品删除请求",ids);
//        boolean success = dishService.deleteByIds(ids);

        if (dishService.getById(ids).getStatus()!=0){
            log.info("菜品删除失败，IDs：{}", ids);
            return R.error("菜品为起售状态，删除失败");
        }
        dishService.removeById(ids);
        dishFlavorService.removeById(ids);
        log.info("菜品删除成功，IDs：{}", ids);



        return R.success("菜品删除成功");
    }

    /**
     * 根据条件查询对应的菜品数据
     * @param dish
     * @return

    @GetMapping("/list")
    public R<List<Dish>> list(Dish dish){
        log.info("根据条件查询对应的菜品数据");
        //构造查询条件
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(dish.getCategoryId() != null, Dish::getCategoryId, dish.getCategoryId());
        queryWrapper.eq(Dish::getStatus,1);
        //添加排序条件
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);

        List<Dish> list = dishService.list(queryWrapper);

        return R.success(list);

        */

    /**
    * 根据条件查询对应的菜品数据
    * @param dish
    * @return
    */
    @GetMapping("/list")
    public R<List<DishDto>> list(Dish dish){
        log.info("根据条件查询对应的菜品数据");
        List<DishDto> dishDtoList = null;

        // 动态构造key
        String key = "dish_" + dish.getCategoryId() + "_" + dish.getStatus();
        //先从redis中获取缓存数据
        dishDtoList = (List<DishDto>) redisTemplate.opsForValue().get(key);

        if (dishDtoList != null){
            //如果存在，直接返回，无需查询Mysql数据库
            return R.success(dishDtoList);
        }

        //如果不存在，需查询Mysql数据库，将查询到的菜品数据缓存到Redis


        //构造查询条件
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(dish.getCategoryId() != null, Dish::getCategoryId, dish.getCategoryId());
        queryWrapper.eq(Dish::getStatus,1);
        //添加排序条件
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);

        List<Dish> list = dishService.list(queryWrapper);

        // 将Dish对象转换为DishDto对象，并设置每个DishDto的categoryName
        dishDtoList = list.stream().map((item) -> {
            DishDto dishDto = new DishDto();

            // 对象属性拷贝
            BeanUtils.copyProperties(item,dishDto);

            // 获取菜品的分类Id
            Long categoryId = item.getCategoryId();
            // 根据分类Id查询分类对象
            Category category = categoryService.getById(categoryId);
            if (category != null){
                // 获取分类名称
                String categoryName = category.getName();
                // 设置菜品的分类名称
                dishDto.setCategoryName(categoryName);
            }

            //当前菜品的ID
            Long dishId = item.getId();
            LambdaQueryWrapper<DishFlavor> queryWrapper1 = new LambdaQueryWrapper<>();
            queryWrapper1.eq(DishFlavor::getDishId,dishId);
            //SQL: select from dish_flavor where dish_id = ?
            List<DishFlavor> dishFlavorList = dishFlavorService.list(queryWrapper1);

            dishDto.setFlavors(dishFlavorList);
            return dishDto;

        }).collect(Collectors.toList());

        // 将查询到的菜品数据缓存到Redis,缓存时间60min
        redisTemplate.opsForValue().set(key,dishDtoList,60, TimeUnit.MINUTES);


        return R.success(dishDtoList);
    }




}














package com.itheima.reggie_takeout.dto;

import com.itheima.reggie_takeout.entity.Dish;
import com.itheima.reggie_takeout.entity.DishFlavor;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

// DishDto = Dish + DishFlavor
@Data
public class DishDto extends Dish {

    private List<DishFlavor> flavors = new ArrayList<>();

    private String categoryName;

    private Integer copies;
}

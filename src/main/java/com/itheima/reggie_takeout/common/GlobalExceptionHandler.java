package com.itheima.reggie_takeout.common;

// 全局异常处理

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLIntegrityConstraintViolationException;

@ControllerAdvice(annotations = {RestController.class, Controller.class})
@ResponseBody
@Slf4j
public class GlobalExceptionHandler {

    //异常处理方法 重复异常
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public R<String> exceptionHandler(SQLIntegrityConstraintViolationException ex){
        log.error(ex.getMessage());

        // 新增员工重复账号、新增菜品分类也能生效
        // 示例：2024-04-07T10:40:17.818+08:00 ERROR 26044 --- [reggie_takeout] [nio-8080-exec-5]
        // c.i.r.common.GlobalExceptionHandler : Duplicate entry 'zou' for key 'employee.idx_username'
        if(ex.getMessage().contains("Duplicate entry")){
            String[] split = ex.getMessage().split(" ");
            String msg = split[2] + "账号已存在";
            return R.error(msg);
        }
        return R.error("未知错误");
    }

    //异常处理方法 自定义业务异常
    @ExceptionHandler(CustomException.class)
    public R<String> exceptionHandler(CustomException ex){
        log.error(ex.getMessage());

        // 删除菜品分类、套餐时的异常
        return R.error(ex.getMessage());
    }
}

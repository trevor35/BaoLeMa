package com.itheima.reggie_takeout.common;

/**
 * @Author zou
 * @Date 2024/4/8
 * @Update 2024/4/8
 * @Description 自定义业务异常类
 */
public class CustomException extends RuntimeException{
    public CustomException(String message){
        super(message);
    }
}

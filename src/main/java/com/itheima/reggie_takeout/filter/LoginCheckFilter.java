package com.itheima.reggie_takeout.filter;


import com.alibaba.fastjson.JSON;
import com.itheima.reggie_takeout.common.BaseContext;
import com.itheima.reggie_takeout.common.R;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.stereotype.Service;
import org.springframework.util.AntPathMatcher;

import java.io.IOException;

@Slf4j
// 检查用户是否已经完成登录
@WebFilter(filterName = "loginCheckFilter",urlPatterns = "/*")
public class LoginCheckFilter implements Filter{
    //路径匹配器，支持通配符写法
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        // 1、获取本次请求的URI
        String requestURI = request.getRequestURI();
        // 定义不需要处理的请求路径
        String[] urls = new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",   //可访问页面（静态资源请求，直接放行）
                "/front/**",
                "/common/**",
                "/user/sendMsg",    //移动端发送短信
                "/user/login",       //移动端登录
        };

        // 2、判断本次请求是否需要处理
        boolean check = check(urls, requestURI);

        // 3、如果不需要处理，则直接放行
        if (check){
            filterChain.doFilter(request, response);
            log.info("请求{}不需要处理，直接放行",requestURI);
            return;
        }

        // 4-1、判断员工登录状态，如果已登录，则直接放行
        if(request.getSession().getAttribute("employee") != null){
            log.info("用户已登录，Filter放行,用户id为{}",request.getSession().getAttribute("employee"));

            Long empId = (Long) request.getSession().getAttribute("employee");
            BaseContext.setCurrentId(empId);

            long id = Thread.currentThread().getId();
            log.info("线程id为：{}",id);

            filterChain.doFilter(request, response);
            return;
        }


        // 4-2、判断用户登录状态，如果已登录，则直接放行
        if(request.getSession().getAttribute("user") != null){
            log.info("用户已登录，Filter放行,用户id为{}",request.getSession().getAttribute("user"));

            Long UserId = (Long) request.getSession().getAttribute("user");
            BaseContext.setCurrentId(UserId);

            long id = Thread.currentThread().getId();
            log.info("线程id为：{}",id);

            filterChain.doFilter(request, response);
            return;
        }

        // 5、如果未登录则返回未登录结果，通过输出流方式向客户端页面响应数据
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        log.info("{}:用户未登录，拦截请求",requestURI);
        return;
    }

    // 路径匹配 检查本次请求是否放行
    public boolean check(String[] urls, String requestURI){
        for (String url : urls) {
            boolean match = PATH_MATCHER.match(url, requestURI);
            if (match){
                return true;
            }
        }
        return false;
    }
}

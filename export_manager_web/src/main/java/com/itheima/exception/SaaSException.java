package com.itheima.exception;

import org.apache.shiro.authz.UnauthorizedException;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SaaSException implements HandlerExceptionResolver{
    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        //如果是UnauthorizedException异常，跳转到unauthorized.jsp
        ModelAndView modelAndView = new ModelAndView();
        //判断，如果是 UnauthorizedException 转发到 页面
        if (ex instanceof UnauthorizedException){
            //请求转发，不通过视图解析器
            modelAndView.setViewName("forward:/unauthorized.jsp");
        }else {
            modelAndView.setViewName("error");
        }
        modelAndView.addObject("errorMsg",ex.getMessage());
        return modelAndView;
    }
}

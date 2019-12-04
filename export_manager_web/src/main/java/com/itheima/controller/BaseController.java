package com.itheima.controller;

import com.itheima.domain.system.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Controller
public class BaseController {
    @Autowired
    protected HttpServletRequest request;
    @Autowired
    protected HttpServletResponse response;
    @Autowired
    protected HttpSession session;


    //获取当前登录人信息
    protected User getUser(){
        return (User) session.getAttribute("loginUser");
    }
    //获取当前登录人所在公司id
    protected String getCompanyId(){
        if (getUser().getCompanyId() != null){
            return getUser().getCompanyId();
        }
        return null;
    }

    // 获取当前登录人所在企业名称
    protected String getCompanyName(){
        if (getUser().getCompanyName() != null){
            return getUser().getCompanyName();
        }
        return null;
    }
}

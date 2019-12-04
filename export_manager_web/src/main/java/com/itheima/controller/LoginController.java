package com.itheima.controller;


import com.itheima.domain.system.Module;
import com.itheima.domain.system.User;
import com.itheima.service.system.ModuleService;
import com.itheima.service.system.UserService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
public class LoginController {
    @Autowired
    private UserService userService;
    @Autowired
    private HttpServletRequest request;
    @Autowired
    private HttpSession session;
    @Autowired
    private ModuleService moduleService;

    @RequestMapping("/login")
    public String login(String email, String password) {
        //用户名或密码不能为空
        if (StringUtils.isEmpty(email) || StringUtils.isEmpty(password)) {
            request.setAttribute("error", "邮箱或密码为空");
            return "forward:/login.jsp";
        }
        //使用shiro框架的认证：创建令牌，获取主题，开始认证

        //将页面传过来的密码转换为密文密码
        password = new Md5Hash(password, email, 2).toString();
//        创建令牌
        UsernamePasswordToken token = new UsernamePasswordToken(email, password);
//        获取主题
        Subject subject = SecurityUtils.getSubject();
//        开始认证
        try {
            subject.login(token);
        } catch (AuthenticationException e) {
            e.printStackTrace();
            request.setAttribute("error", "邮箱或密码错误");
            return "forward:login.jsp";
        }
        //从shiro中获取当前登录人
        User user = (User) subject.getPrincipal();
        //将登录人信息存储到session域
        //session.setAttribute("loginUser",user);
        //根据用户查询菜单
        //List<Module> moduleList = moduleService.findModuleListByUser(user);
        //session.setAttribute("modules",moduleList);
        //return "home/main";
        /*根据email查询用户
        User user = userService.findByEmail(email);
        if (user == null) {
            request.setAttribute("error", "邮箱错误");
            return "forward:/login.jsp";
        } else {
            //如果能查询到，将页面传过来的密码加密后和user的密码比较
            String password_db = user.getPassword();
            String password_page = new Md5Hash(password, email, 2).toString();
            if (!password_db.equals(password_page)){
                request.setAttribute("error","密码错误");
                return "forward:/login.jsp";
            }
        }*/
        //将用户信息保存session中
        session.setAttribute("loginUser", user);
        //根据登录用户查询模块信息
        List<Module> moduleList = moduleService.findModuleListByUser(user);
        session.setAttribute("modules", moduleList);
        return "home/main";
    }

    //退出
    @RequestMapping(value = "/logout", name = "用户登出")
    public String logout() {
        SecurityUtils.getSubject().logout();   //通知shiro 登出
        //session.removeAttribute("loginUser");  //普通退出清除session
        return "forward:login.jsp";
    }

    @RequestMapping("/home")
    public String home() {
        return "home/home";
    }

}




package com.itheima.controller.log;

import com.itheima.domain.system.SysLog;
import com.itheima.domain.system.User;
import com.itheima.service.system.SysLogService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.UUID;

@Component
@Aspect
public class AspectLog {
    @Autowired
    private SysLogService sysLogService;
    @Autowired
    private HttpServletRequest request;
    @Autowired
    private HttpSession session;
    @Around("execution(* com.itheima.controller.*.*.*(..))")
    public Object saveLog(ProceedingJoinPoint pjp)throws Throwable{
        SysLog sysLog = new SysLog();
        sysLog.setTime(new Date());
        sysLog.setIp(request.getRemoteAddr());
        sysLog.setId(UUID.randomUUID().toString());
        User user = (User) session.getAttribute("loginUser");
        if (user != null){
            sysLog.setUserName(user.getUserName());
            sysLog.setCompanyId(user.getCompanyId());
            sysLog.setCompanyName(user.getCompanyName());
        }
        //获取方法签名（方法+注解）
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        //从方法签名中获取方法对象
        Method method = signature.getMethod();
        String methodName = method.getName();//方法名
        sysLog.setMethod(methodName);
        //判断方法上是否有此requestMapping注解
        if (method.isAnnotationPresent(RequestMapping.class)){
            //获取注解
            RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
            String annotationName = requestMapping.name();
            sysLog.setAction(annotationName);
        }
        sysLogService.save(sysLog);
        return pjp.proceed();//运行原方法
    }
}

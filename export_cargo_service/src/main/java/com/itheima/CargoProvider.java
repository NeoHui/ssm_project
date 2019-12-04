package com.itheima;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class CargoProvider {
    public static void main(String[] args) throws Exception {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath*:spring/applicationContext-*.xml");
        context.start();
        System.in.read(); // 按任意键退出
    }
}

package com.itheima.test;


import org.apache.shiro.crypto.hash.Md5Hash;
import org.junit.Test;

import java.io.IOException;

public class TestDemo {
    @Test
    public void test01() throws IOException {
        String pswd = new Md5Hash("111111", "123456@export.com", 2).toString();
        System.out.println(pswd);
        System.in.read();
    }
}

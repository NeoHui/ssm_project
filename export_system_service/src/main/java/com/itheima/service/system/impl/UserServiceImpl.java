package com.itheima.service.system.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.itheima.dao.system.UserDao;
import com.itheima.domain.system.User;
import com.itheima.domain.system.UserBirthday;
import com.itheima.service.system.UserService;
import com.itheima.utils.MailUtil;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserDao userDao;

    //进入用户管理页面
    @Override
    public PageInfo<User> findPage(String companyId, Integer paegNum, Integer pageSize) {
        PageHelper.startPage(paegNum, pageSize);
        List<User> list = userDao.findAll(companyId);
        return new PageInfo<User>(list);
    }

    @Autowired//整合MQ发送
    private AmqpTemplate amqpTemplate;

    //新增用户
    @Override
    public void save(User user) throws MessagingException {
        String passwordMing = user.getPassword();
        String password = user.getPassword();
        password = new Md5Hash(password, user.getEmail(), 2).toString();
        user.setPassword(password);
        userDao.save(user);

        //spring整合发邮件
        /*try {
            springMailUtil.sendEmail(user.getEmail(), "恭喜新员工", "您现在可以使用saas了，密码是：" + passwordMing);
        } catch (MessagingException e) {
            e.printStackTrace();
            System.out.println("邮件发送失败");
        }*/
        //整合MQ发送邮件
        Map<String, String> map = new HashMap();
        map.put("to", user.getEmail());
        map.put("subject", "恭喜新员工");
        map.put("content", "您现在可以使用saas了，密码是" + passwordMing);
        amqpTemplate.convertAndSend("user.insert", map);

        //mailUtil 方式发送邮件
       /* try {
            MailUtil.sendMsg(user.getEmail(),"恭喜新员工","您现在可以使用saas了，密码是："+ passwordMing);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("邮件发送失败");
        }*/
    }

    //修改用户
    @Override
    public void update(User user) {
        userDao.update(user);
    }

    @Override
    public User findById(String id) {
        return userDao.findById(id);
    }

    @Override
    public void delete(String id) {
        userDao.delete(id);
    }

    @Override
    public List<String> findRoleIdsByUserId(String id) {
        return userDao.findRoleIdsByUserId(id);
    }

    @Override
    public void changeRole(String userid, String[] roleIds) {
        //删除用户之前的角色数据
        userDao.deleteRoleAndUserByUserId(userid);
        //向中间表插入数据
        for (String roleId : roleIds) {
            userDao.saveUserAndRole(userid, roleId);
        }
    }

    @Override
    public User findByEmail(String email) {
       return userDao.findByEmail(email);
    }

    public void sendBirthdayEmail() {
        List<UserBirthday> userBirthdays = userDao.findBirthday();
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd");
        String format = dateFormat.format(date);
        for (UserBirthday user : userBirthdays) {
            String name = user.getName();
            String email = user.getEmail();
            String birthday = user.getBirthday();
            if (birthday.contains(format)) {
                Map<String, String> map = new HashMap();
                map.put("to", email);
                map.put("subject", "生日祝福");
                map.put("content", "尊敬的用户："+ name +"，生日快乐");
                amqpTemplate.convertAndSend("user.insert", map);
            }

        }

    }


}

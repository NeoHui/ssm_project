package com.itheima.service.system;

import com.github.pagehelper.PageInfo;
import com.itheima.domain.system.User;
import com.itheima.domain.system.UserBirthday;

import javax.mail.MessagingException;
import java.util.List;

public interface UserService {
    PageInfo<User> findPage(String companyId, Integer paegNum, Integer pageSize);

    void save(User user) throws MessagingException;

    void update(User user);

    User findById(String id);

    void delete(String id);

    List<String> findRoleIdsByUserId(String id);

    void changeRole(String userid, String[] roleIds);

    User findByEmail(String email);
}

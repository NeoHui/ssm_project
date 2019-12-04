package com.itheima.dao.system;

import com.itheima.domain.system.User;
import com.itheima.domain.system.UserBirthday;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface UserDao {
    //显示用户信息查询所有
    List<User> findAll(String companyId);
    //新增用户
    void save(User user);
    //修改用户信息
    void update(User user);
    //根据id查询
    User findById(String id);
    //删除用户信息
    void delete(String id);
    //根据用户id查找用户角色信息ids
    List<String> findRoleIdsByUserId(String id);
    //根据用户id删除用户所有角色
    void deleteRoleAndUserByUserId(String userid);
    //分配用户角色
    void saveUserAndRole(@Param("userid") String userid,@Param("roleId") String roleId);

    User findByEmail(String email);


    void sendBirthdayEmail();

    List<UserBirthday> findBirthday();
}

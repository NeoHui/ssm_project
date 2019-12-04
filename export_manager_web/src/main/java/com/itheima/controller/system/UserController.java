package com.itheima.controller.system;

import com.github.pagehelper.PageInfo;
import com.itheima.controller.BaseController;
import com.itheima.domain.system.Dept;
import com.itheima.domain.system.Role;
import com.itheima.domain.system.User;
import com.itheima.service.system.DeptService;
import com.itheima.service.system.RoleService;
import com.itheima.service.system.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.mail.MessagingException;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/system/user")
public class UserController extends BaseController {
    @Autowired
    private UserService userService;
    @Autowired
    private DeptService deptService;
    @Autowired
    private RoleService roleService;

    @RequestMapping(value = "/list", name = "进入用户管理页面")
    public String findAll(@RequestParam(name = "page",defaultValue = "1") Integer paegNum, @RequestParam(name = "pageSize",defaultValue = "10") Integer pageSize) {
        PageInfo<User> pageInfo = userService.findPage(getCompanyId(), paegNum, pageSize);
        request.setAttribute("page", pageInfo);
        return "system/user/user-list";
    }

    @RequestMapping(value = "/toAdd", name = "进入用户新增页面")
    public String toAdd() {
        //查询所有本用户的部门数据
        List<Dept> deptList = deptService.findAll(getCompanyId());
        request.setAttribute("deptList", deptList);
        return "system/user/user-add";
    }

    @RequestMapping(value = "/edit",name = "用户新增方法")
    public String edit(User user) throws Exception {
        //user的id为空就是新增，反之就是修改
        if (StringUtils.isEmpty(user.getId())){
            //id为空，此时新增，需要给id赋值
            user.setCompanyId(getCompanyId());
            user.setCompanyName(getCompanyName());
            user.setCreateTime(new Date());
            //将id赋值成一个随机值
            user.setId(UUID.randomUUID().toString());
            userService.save(user);
        }else {//id不为空时  修改
            user.setCreateTime(new Date());
            userService.update(user);
        }
        return "redirect:/system/user/list.do";
    }

    @RequestMapping(value = "/toUpdate",name = "进入修改保存页面")
    public String toUpdate(String id){
        //根据id查询用户
        User user = userService.findById(id);
        request.setAttribute("user",user);
        //查询用户的本部门所有数据
        List<Dept> deptList = deptService.findAll(getCompanyId());
        request.setAttribute("deptList",deptList);
        return "system/user/user-add";
}

    @RequestMapping(value = "/delete",name = "删除用户数据")
    public String detele(String id){
        userService.delete(id);
        return "redirect:/system/user/list.do";
    }

    @RequestMapping(value = "/roleList",name ="进入给用户分配角色页面" )
    public String roleList(String id){
        //本企业的所有角色
        List<Role> roleList = roleService.findAll(getCompanyId());
        request.setAttribute("roleList",roleList);
        //当前用户对象
        User user = userService.findById(id);
        request.setAttribute("user",user);
        //当前用户拥有的角色ids字符串
        List<String> roleIdList = userService.findRoleIdsByUserId(id);
        String userRoleStr = roleIdList.stream().collect(Collectors.joining(","));
        request.setAttribute("userRoleStr",userRoleStr);
        return "system/user/user-role";
    }

    @RequestMapping(value = "/changeRole",name = "更改用户权限")
    public String changeRole(String userid,String[] roleIds){
        userService.changeRole(userid,roleIds);
        return "redirect:/system/user/list.do";
    }
}

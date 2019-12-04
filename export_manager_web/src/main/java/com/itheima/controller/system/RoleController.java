package com.itheima.controller.system;

import com.github.pagehelper.PageInfo;
import com.itheima.controller.BaseController;
import com.itheima.domain.system.Module;
import com.itheima.domain.system.Role;
import com.itheima.service.system.ModuleService;
import com.itheima.service.system.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

@Controller
@RequestMapping("/system/role")
public class RoleController extends BaseController {
    @Autowired
    private RoleService roleService;
    @Autowired
    private ModuleService moduleService;

    @RequestMapping(value = "roleModule", name = "进入到角色分配权限页面")
    public String roleModule(String roleid) {
        Role role = roleService.findById(roleid);
        request.setAttribute("role", role);
        return "system/role/role-module";
    }

    @RequestMapping(value = "/list", name = "进入角色管理页面")
    public String findAll(@RequestParam(name = "page", defaultValue = "1") Integer pageNum, @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        PageInfo pageInfo = roleService.findPage(getCompanyId(), pageNum, pageSize);
        request.setAttribute("page", pageInfo);
        return "system/role/role-list";
    }

    @RequestMapping(value = "/toAdd", name = "进入新增角色页面")
    public String toAdd() {
        return "system/role/role-add";
    }

    @RequestMapping(value = "/edit", name = "新增角色方法")
    public String edit(Role role) {
        //role的id如果为空就是新增，反之就是修改
        if (StringUtils.isEmpty(role.getId())) {
            role.setId(UUID.randomUUID().toString());
            role.setCompanyId(getCompanyId());
            role.setCompanyName(getCompanyName());
            role.setCreateTime(new Date());
            roleService.save(role);
        } else {
            roleService.update(role);
        }
        return "redirect:/system/role/list.do";
    }

    @RequestMapping(value = "/toUpdate", name = "进入修改角色页面")
    public String toUpdate(String id) {
        Role role = roleService.findById(id);
        request.setAttribute("role", role);
        return "system/role/role-add";
    }

    @RequestMapping(value = "/delete", name = "删除角色")
    public String delete(String id) {
        roleService.deleteById(id);
        return "redirect:/system/role/list.do";
    }

    @RequestMapping(value = "/getZtreeNodes", name = "构造模块数据")
    @ResponseBody
    public List<Map> getZtreeNodes(String roleid){
        //查询此角色拥有的菜单权限
        List<String> moduleIdList = moduleService.findModulesByRoleId(roleid);
        //查询所有子模块
        List<Module> moduleList = moduleService.findAll();
        //构造map集合
        ArrayList<Map> list = new ArrayList<>();
        Map map = null;
        for (Module module : moduleList) {
            //初始化map
           map = new HashMap<>();
            //添加map中的数据
            map.put("id",module.getId());   //模块id
            map.put("pId",module.getParentId());  //父模块id
            map.put("name",module.getName()); //模板名称
            if (moduleIdList.contains(module.getId())){
                map.put("checked",true);
            }
            //存入list集合
            list.add(map);
        }
        return list;
    }

    @RequestMapping(value = "/updateRoleModule",name = "分配权限")
    public String updateRoleModule(String roleid,String moduleIds){
        roleService.updateRoleModule(roleid,moduleIds);
        //跳转到角色列表
        return "redirect:/system/role/list.do";
    }
}

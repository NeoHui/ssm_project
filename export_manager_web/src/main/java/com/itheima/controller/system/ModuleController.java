package com.itheima.controller.system;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.itheima.controller.BaseController;
import com.itheima.domain.system.Module;
import com.itheima.service.system.ModuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

@Controller
@RequestMapping("/system/module")
public class ModuleController extends BaseController{
    @Autowired
    private ModuleService moduleService;

    @RequestMapping(value = "/list",name ="进入菜单管理页面" )
    public String findAll(@RequestParam(name = "page",defaultValue = "1") Integer pageNum,@RequestParam(name = "pageSize",defaultValue = "10") Integer pageSize){
        PageInfo<Module> pageInfo = moduleService.findPage(pageNum,pageSize);
        request.setAttribute("page",pageInfo);
        return "system/module/module-list";
    }

    @RequestMapping(value = "/toAdd",name = "进入菜单新增页面")
    public String toAdd(){
        //查询所有本菜单的菜单数据
        List<Module> moduleList = moduleService.findAll();
        request.setAttribute("menus",moduleList);
        return "system/module/module-add";
    }

    @RequestMapping(value = "/edit",name = "保存菜单方法")
    public String edit(Module module){
        //module的id值如果为空就是新增，否则为修改
        if (StringUtils.isEmpty(module.getId())){
            module.setId(UUID.randomUUID().toString());
            moduleService.save(module);
        }else {
            moduleService.update(module);
        }
        //重定向到列表数据
        return "redirect:/system/module/list.do";
    }
    @RequestMapping(value = "/toUpdate",name = "进入菜单修改页面")
    public String toUpdate(String id){
        //根据id查询菜单
        Module module = moduleService.findById(id);
        //将module放入request域
        request.setAttribute("module",module);
        List<Module> moduleList = moduleService.findAll();
        request.setAttribute("menus",moduleList);
        return "system/module/module-add";
    }
    @RequestMapping(value = "/delete",name = "菜单删除")
    public String delete(String id){
        moduleService.deleteById(id);
        return "redirect:/system/module/list.do";
    }
}

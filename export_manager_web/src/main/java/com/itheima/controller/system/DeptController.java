package com.itheima.controller.system;

import com.github.pagehelper.PageInfo;
import com.itheima.controller.BaseController;
import com.itheima.domain.system.Dept;
import com.itheima.service.system.DeptService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/system/dept")
public class DeptController extends BaseController {
    @Autowired
    private DeptService deptService;

    @RequestMapping(value = "/list", name = "进入部门管理页面")
    @RequiresPermissions("查看部门")
    public String findAll(@RequestParam(name = "page", defaultValue = "1") Integer pageNum, @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        PageInfo<Dept> pageInfo = deptService.findPage(getCompanyId(), pageNum, pageSize);
        request.setAttribute("page", pageInfo);
        return "system/dept/dept-list";
    }

    @RequestMapping(value = "/toAdd", name = "进入部门新增页面")
    @RequiresPermissions("新增部门")
    public String toAdd() {
        List<Dept> deptList = deptService.findAll(getCompanyId());
        request.setAttribute("deptList", deptList);
        return "system/dept/dept-add";
    }

    //新增
    @RequestMapping(value = "/edit", name = "保存部门方法")
    @RequiresPermissions("新增部门")
    public String edit(Dept dept) {
        if (dept.getParent().getId().equals("")) {
            dept.getParent().setId(null);
        }
        //dept的id如果为空就是新增，反之就是修改
        if (StringUtils.isEmpty(dept.getId())) {
            dept.setCompanyId(getCompanyId());
            dept.setCompanyName(getCompanyName());
            dept.setId(UUID.randomUUID().toString());
            deptService.save(dept);
        } else {
            deptService.update(dept);
        }
        return "redirect:/system/dept/list.do";
    }

    //修改部门信息
    @RequestMapping(value = "/toUpdate", name = "进入修改部门页面")
    @RequiresPermissions("修改部门")
    public String toUpdate(String id) {
        //根据id查询部门
        Dept dept = deptService.findById(id);
        //将dept放入request域
        request.setAttribute("dept", dept);
        List<Dept> deptList = deptService.findAll(getCompanyId());
        request.setAttribute("deptList", deptList);
        //修改页面和新增页面公用一套
        return "system/dept/dept-add";
    }

    //删除部门信息
    @RequestMapping(value = "/delete", name = "删除部门列表信息")
    @RequiresPermissions("删除部门")
    public String delete(String id) {
        deptService.delete(id);
        //重定向到列表数据
        return "redirect:/system/dept/list.do";
    }
}

package com.itheima.controller.company;

import com.alibaba.dubbo.config.annotation.Reference;
import com.github.pagehelper.PageInfo;
import com.itheima.controller.BaseController;
import com.itheima.domain.company.Company;
import com.itheima.service.company.CompanyService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@Controller
@RequestMapping("/company")
public class CompanyController extends BaseController{
    @Reference
    private CompanyService companyService;

    //查询企业信息
    @RequestMapping(value = "/list",name = "进入企业管理页面")
    @RequiresPermissions("企业管理")//有 企业管理权限才能进入此方法
    public String findAll(@RequestParam(name = "page", defaultValue = "1") int pageNum,@RequestParam(name = "pageSize",defaultValue = "5") int pageSize) {
        //List<Company> list = companyService.findAll();
        PageInfo<Company> pageInfo = companyService.findPage(pageNum,pageSize);
        request.setAttribute("page", pageInfo);
        return "company/company-list";
    }

    @RequestMapping(value = "/toAdd",name = "进入企业新增页面")
    public String toAdd(){
        return "/company/company-add";
    }

    //新增
    @RequestMapping(value = "/edit",name = "保存企业方法")
    public String edit(Company company){
        //company的id如果为空就是新增，反之就是修改
        if (StringUtils.isEmpty(company.getId())) {
            company.setId(UUID.randomUUID().toString());
            companyService.save(company);
        }else {
            companyService.update(company);
        }
        return "redirect:/company/list.do";
    }

    //修改企业信息
    @RequestMapping(value = "/toUpdate",name = "进入修改企业页面")
    public String toUpdate(String id){
        //根据id查询企业
        Company company = companyService.findById(id);
        request.setAttribute("company",company);
        return "/company/company-add";
    }

    //删除企业信息
    @RequestMapping(value = "/delete" ,name = "删除企业列表信息")
    public String delete(String id){
        companyService.delete(id);
        return "redirect:/company/list.do";
    }
}

package com.itheima.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.domain.company.Company;
import com.itheima.service.company.CompanyService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.UUID;

@Controller
public class ApplyController {
    @Reference
    private CompanyService companyService;
    @RequestMapping("/apply")
    @ResponseBody
    public String apply(Company company){
        try {
            company.setId(UUID.randomUUID().toString());
            company.setState(0);
            companyService.save(company);
            return "1";//申请成功
        } catch (Exception e) {
            e.printStackTrace();
            return "0";//失败
        }
    }
}

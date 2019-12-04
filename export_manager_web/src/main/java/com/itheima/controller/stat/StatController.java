package com.itheima.controller.stat;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.controller.BaseController;
import com.itheima.stat.service.StatService;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/stat")
public class StatController extends BaseController {
    @RequestMapping(value = "/toCharts", name = "跳转到统计页面")
    public String toCharts(String chartsType) {
        return "/stat/stat-" + chartsType;
    }

    @Reference
    private StatService statService;

    @RequestMapping(value = "/factoryCharts", name = "每个厂家销售额统计")
    @ResponseBody
    public List<Map> factoryCharts() {
        return statService.factoryCharts(getCompanyId());
    }

    @RequestMapping(value = "/sellCharts", name = "货物销量统计")
    @ResponseBody
    public List<Map> sellCharts() {
        return statService.sellCharts(getCompanyId());
    }

    @RequestMapping(value = "/onlineCharts",name = "24小时系统访问量")
    @ResponseBody
    public List<Map> onlineCharts(){
        return statService.onlineCharts(getCompanyId());
    }
}


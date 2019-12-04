package com.itheima.controller.system;

import com.github.pagehelper.PageInfo;
import com.itheima.controller.BaseController;
import com.itheima.service.system.SysLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("system/log")
public class SysLogController extends BaseController {
    @Autowired
    private SysLogService sysLogService;
    //查询日志列表
    @RequestMapping(value = "/list",name ="系统日志列表" )
    public String list(@RequestParam(name = "page",defaultValue = "1")int pageNum,@RequestParam(name = "pageSize",defaultValue = "10")int pageSize){
        PageInfo page = sysLogService.findPage(getCompanyId(), pageNum, pageSize);
        request.setAttribute("page",page);
        return "/system/log/log-list";
    }
}

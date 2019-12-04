package com.itheima.service.system;

import com.github.pagehelper.PageInfo;
import com.itheima.domain.system.SysLog;

public interface SysLogService {
    //新增保存
    public void save(SysLog log);
    //查询所有（分页）
    public PageInfo findPage(String companyId, int pageNum, int pageSize);
}

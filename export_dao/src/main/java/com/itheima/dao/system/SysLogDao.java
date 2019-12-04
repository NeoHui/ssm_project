package com.itheima.dao.system;

import com.itheima.domain.system.SysLog;

import java.util.List;

public interface SysLogDao {
    //查询所有
    List<SysLog> findAll(String companyId);
    //添加
    int save(SysLog log);
}

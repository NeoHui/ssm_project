package com.itheima.service.system.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.itheima.dao.system.SysLogDao;
import com.itheima.domain.system.SysLog;
import com.itheima.service.system.SysLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SysLogServiceImpl implements SysLogService {
    @Autowired
    private SysLogDao sysLogDao;
    @Override
    public void save(SysLog log) {
        sysLogDao.save(log);
    }

    @Override
    public PageInfo findPage(String companyId, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        List<SysLog> list = sysLogDao.findAll(companyId);
        return new PageInfo(list,5);
    }
}

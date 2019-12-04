package com.itheima.service.company.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.itheima.dao.company.CompanyDao;
import com.itheima.domain.company.Company;
import com.itheima.service.company.CompanyService;
import org.springframework.beans.factory.annotation.Autowired;


import java.util.List;

@Service
public class CompanyServiceImpl implements CompanyService {
    @Autowired
    private CompanyDao companyDao;
    @Override
    public List<Company> findAll() {
        return companyDao.findAll();
    }

    @Override
    public void save(Company company) {
       companyDao.save(company);
    }

    @Override
    public Company findById(String id) {
        return companyDao.findById(id);
    }

    @Override
    public void update(Company company) {
        companyDao.update(company);
    }

    @Override
    public void delete(String id) {
        companyDao.delete(id);
    }

    @Override
    public PageInfo<Company> findPage(int pageNum, int pageSize) {
        /*//查询总条数(传统分页查询)
        Long total = companyDao.selectCount();
        //查询当前页的数据
        List list = companyDao.findPage((pageNum-1)*pageSize,pageSize);
        PageBean pageBean = new PageBean(pageNum,pageSize, list,total);*/

        //pageHelper查询
        PageHelper.startPage(pageNum,pageSize);//使用分页插件 一定要紧跟一个查询方法
        List<Company> list = companyDao.findAll();
        return new PageInfo<Company>(list,5);
    }
}

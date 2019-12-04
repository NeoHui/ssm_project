package com.itheima.service.cargo.impl;

import com.itheima.dao.cargo.ExtEproductDao;
import com.itheima.domain.cargo.ExtEproduct;
import com.itheima.domain.cargo.ExtEproductExample;
import com.itheima.service.cargo.ExtEproductService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;

import java.util.List;

@Service
public class ExtEproductServiceImpl implements ExtEproductService {

    @Autowired
    private ExtEproductDao extEproductDao;

    @Override
    public ExtEproduct findById(String id) {
        return extEproductDao.selectByPrimaryKey(id);
    }

    @Override
    public void save(ExtEproduct extEproduct) {
        extEproductDao.insertSelective(extEproduct);
    }

    @Override
    public void update(ExtEproduct extEproduct) {
        extEproductDao.updateByPrimaryKeySelective(extEproduct);
    }

    @Override
    public void delete(String id) {
        extEproductDao.deleteByPrimaryKey(id);
    }

    @Override
    public PageInfo findAll(ExtEproductExample extEproductExample, int page, int size) {
        PageHelper.startPage(page,size);
        List<ExtEproduct> list = extEproductDao.selectByExample(extEproductExample);
        return new PageInfo(list);
    }
}

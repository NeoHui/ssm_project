package com.itheima.service.system;

import com.github.pagehelper.PageInfo;
import com.itheima.domain.system.Dept;

import java.util.List;

public interface DeptService {


    void save(Dept dept);

    Dept findById(String id);

    void update(Dept dept);

    void delete(String id);

    PageInfo<Dept> findPage(String companyId, Integer pageNum, Integer pageSize);

    List<Dept> findAll(String companyId);
}

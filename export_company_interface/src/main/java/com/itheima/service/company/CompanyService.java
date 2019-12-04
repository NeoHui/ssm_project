package com.itheima.service.company;

import com.github.pagehelper.PageInfo;
import com.itheima.domain.company.Company;

import java.util.List;

public interface CompanyService {
    public List<Company> findAll();

    void save(Company company);

    Company findById(String id);

    void update(Company company);

    void delete(String id);

    PageInfo<Company> findPage(int pageNum, int pageSize);
}

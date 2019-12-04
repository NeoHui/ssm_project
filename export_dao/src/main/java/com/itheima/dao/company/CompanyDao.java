package com.itheima.dao.company;

import com.itheima.domain.company.Company;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CompanyDao {
    public List<Company> findAll();

    void save(Company company);

    Company findById(String id);

    void update(Company company);

    void delete(String id);

    Long selectCount();

    List findPage(@Param("start") int statrt,@Param("pageSize") int pageSize);
}

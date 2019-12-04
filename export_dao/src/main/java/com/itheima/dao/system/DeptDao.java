package com.itheima.dao.system;


import com.itheima.domain.system.Dept;


import java.util.List;

public interface DeptDao {

    void save(Dept dept);

    Dept findById(String id);

    void update(Dept dept);

    void deleteById(String id);

    List<Dept> findAll(String companyId);
}

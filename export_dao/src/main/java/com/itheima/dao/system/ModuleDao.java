package com.itheima.dao.system;

import com.itheima.domain.system.Module;

import java.util.List;

public interface ModuleDao {

    List<Module> findAll();

    void save(Module module);

    void update(Module module);

    Module findById(String id);

    void deleteById(String id);

    List<String> findModulesByRoleId(String roleid);

    List<Module> findByBelong(int belong);

    List<Module> findByUserId(String id);
}

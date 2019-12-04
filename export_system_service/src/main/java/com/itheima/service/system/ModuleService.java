package com.itheima.service.system;

import com.github.pagehelper.PageInfo;
import com.itheima.domain.system.Module;
import com.itheima.domain.system.User;

import java.util.List;

public interface ModuleService {

    PageInfo<Module> findPage(Integer pageNum, Integer pageSize);

    List<Module> findAll();

    void save(Module module);

    void update(Module module);

    Module findById(String id);

    void deleteById(String id);

    List<String> findModulesByRoleId(String roleid);

    List<Module> findModuleListByUser(User user);
}

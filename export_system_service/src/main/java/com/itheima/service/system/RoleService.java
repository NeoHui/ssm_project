package com.itheima.service.system;

import com.github.pagehelper.PageInfo;
import com.itheima.domain.system.Role;

import java.util.List;

public interface RoleService {

    PageInfo findPage(String companyId, Integer pageNum, Integer pageSize);

    void save(Role role);

    void update(Role role);

    Role findById(String id);

    void deleteById(String id);

    List<Role> findAll(String companyId);

    void updateRoleModule(String roleid, String moduleIds);
}

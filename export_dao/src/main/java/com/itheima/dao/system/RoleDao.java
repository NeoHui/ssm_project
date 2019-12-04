package com.itheima.dao.system;

import com.itheima.domain.system.Role;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface RoleDao {
    List<Role> findAll(String companyId);

    void save(Role role);

    void update(Role role);

    Role findById(String id);

    void deleteById(String id);

    void deleteRoleAndModuleByRoleId(String roleid);

    void saveRoleAndModule(@Param("roleid") String roleid, @Param("moduleId") String moduleId);
}

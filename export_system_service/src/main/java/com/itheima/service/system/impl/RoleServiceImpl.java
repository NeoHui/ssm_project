package com.itheima.service.system.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.itheima.dao.system.RoleDao;
import com.itheima.domain.system.Role;
import com.itheima.service.system.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleServiceImpl implements RoleService {
    @Autowired
    private RoleDao roleDao;

    @Override
    public PageInfo findPage(String companyId, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        List<Role> list = roleDao.findAll(companyId);
        return new PageInfo(list);
    }

    @Override
    public void save(Role role) {
        roleDao.save(role);
    }

    @Override
    public void update(Role role) {
        roleDao.update(role);
    }

    @Override
    public Role findById(String id) {
        return roleDao.findById(id);
    }

    @Override
    public void deleteById(String id) {
        roleDao.deleteById(id);
    }

    @Override
    public List<Role> findAll(String companyId) {
        return roleDao.findAll(companyId);
    }

    @Override
    public void updateRoleModule(String roleid, String moduleIds) {
        //1.删除此角色所具有的所有模块数据,就是根据角色id删除中间表的数据
        roleDao.deleteRoleAndModuleByRoleId(roleid);
        //2 根据字符串获取模块id的数据
        String[] mIds = moduleIds.split(",");
        for (String moduleId : mIds) {
            roleDao.saveRoleAndModule(roleid,moduleId);
        }
    }
}

package com.itheima.service.system.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.itheima.dao.system.ModuleDao;
import com.itheima.domain.system.Module;
import com.itheima.domain.system.User;
import com.itheima.service.system.ModuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Service
@RequestMapping("/system/module")
public class ModuleServiceImpl implements ModuleService {
    @Autowired
    private ModuleDao moduleDao;

    @Override
    public PageInfo<Module> findPage(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        List<Module> list = moduleDao.findAll();
        return new PageInfo<Module>(list,5);
    }

    //查询所有
    @Override
    public List<Module> findAll() {
        return moduleDao.findAll();
    }

    @Override
    public void save(Module module) {
        moduleDao.save(module);
    }

    @Override
    public void update(Module module) {
        moduleDao.update(module);
    }

    @Override
    public Module findById(String id) {
        return moduleDao.findById(id);
    }

    @Override
    public void deleteById(String id) {
        moduleDao.deleteById(id);
    }

    @Override
    public List<String> findModulesByRoleId(String roleid) {
        return moduleDao.findModulesByRoleId(roleid);
    }

    @Override
    public List<Module> findModuleListByUser(User user) {
        //判断用户等级
        if (user.getDegree() == 0){
            //saas管理员，只查询saas系统相关的菜单
            return moduleDao.findByBelong(0);
        }else if (user.getDegree() == 1){
            //租用企业管理员，查询企业所有菜单菜单
            return moduleDao.findByBelong(1);
        }else {
            //租用企业普通员工，根据权限查询菜单
            return moduleDao.findByUserId(user.getId());
        }
    }
}

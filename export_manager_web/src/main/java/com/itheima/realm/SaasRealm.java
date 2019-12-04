package com.itheima.realm;

import com.itheima.domain.system.Module;
import com.itheima.domain.system.User;
import com.itheima.service.system.ModuleService;
import com.itheima.service.system.UserService;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class SaasRealm extends AuthorizingRealm {
    @Autowired
    private UserService userService;
    @Autowired
    private ModuleService moduleService;

    @Override//授权
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {

        System.out.println("-----------------进入授权方法------------------");
        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
        //告诉shiro框架当前登录人有哪些菜单权限
        User user = (User) principalCollection.getPrimaryPrincipal();
        //根据用户查询所拥有的菜单权限
        List<Module> moduleList = moduleService.findModuleListByUser(user);
        for (Module module : moduleList) {
            authorizationInfo.addStringPermission(module.getName());
        }
        return authorizationInfo;
    }

    @Override//认证
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        System.out.println("++++++++++++++进入认证方法++++++++++++++");
        //创建令牌
        UsernamePasswordToken token = (UsernamePasswordToken) authenticationToken;
        String email = token.getUsername();
        String password_page = new String(token.getPassword());
        //从数据库查询用户信息
        User user = userService.findByEmail(email);
        if (user != null){
           //匹配数据库用户密码
            String password_db = user.getPassword();
           if (password_db.equals(password_page)){
               return new SimpleAuthenticationInfo(user,password_db,getName());
           }
        }
        return null;//没有用户
    }
}

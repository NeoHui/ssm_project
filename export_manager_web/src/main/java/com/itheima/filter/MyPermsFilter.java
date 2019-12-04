package com.itheima.filter;

import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authz.AuthorizationFilter;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public class MyPermsFilter extends AuthorizationFilter {
    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) throws Exception {
        Subject subject = this.getSubject(request, response);
        String[] perms = (String[]) mappedValue;
        if (perms != null && perms.length > 0) {
            for (String perm : perms) {
                if (subject.isPermitted(perm)) {
                    return true;
                }
            }
            return false;
        }
        return true;
    }
}

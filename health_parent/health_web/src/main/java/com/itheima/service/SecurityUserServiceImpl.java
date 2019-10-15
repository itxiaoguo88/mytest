package com.itheima.service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.pojo.Permission;
import com.itheima.pojo.Role;
import com.itheima.pojo.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service("securityUserServiceImpl")
public class SecurityUserServiceImpl implements UserDetailsService {
    @Reference
    private UserService userService;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //1.认证
        //1.1通过用户名查询用户是否存在数据库,调用业务服务
         User user= userService.findByusername(username);
        //如果用户不存在就返回null
        if (null==user) {
            return null;
        }
            //2.授权
            List<GrantedAuthority> authorityList = new ArrayList<GrantedAuthority>();
            //用户存在
            Set<Role> userRoles= user.getRoles();
        if (null !=userRoles) {

            GrantedAuthority authority = null;
            for (Role role : userRoles) {
                //创建角色
                authority = new SimpleGrantedAuthority(role.getKeyword());
                //添加用户的角色
                authorityList.add(authority);
                //判断角色下是否有权限
                if (null != role.getPermissions()) {
                    for (Permission p : role.getPermissions()) {
                        //添加用户的权限
                        authority = new SimpleGrantedAuthority(p.getKeyword());
                        authorityList.add(authority);
                    }
                }
            }
        }
        //user.getUsername(),user.getPassword()让security帮我们校验,调用encoder的matches方法,encoder这个是我们配置的bean
        return new  org.springframework.security.core.userdetails.User(user.getUsername(),user.getPassword(),authorityList);
    }
}

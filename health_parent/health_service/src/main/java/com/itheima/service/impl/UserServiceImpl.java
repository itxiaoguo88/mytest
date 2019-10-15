package com.itheima.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.itheima.dao.UserDao;
import com.itheima.pojo.User;
import com.itheima.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;

@Service(interfaceClass = UserService.class)
public class UserServiceImpl implements UserService {
    @Autowired
    private UserDao userDao;
    /**
     * 认证与授权,用户的所有权限
     * @param username
     * @return
     */
    @Override
    public User findByusername(String username) {
        return userDao.findByusername(username);
    }
}

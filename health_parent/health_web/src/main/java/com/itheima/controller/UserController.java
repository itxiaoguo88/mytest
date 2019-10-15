package com.itheima.controller;

import com.itheima.constant.MessageConstant;
import com.itheima.entity.Result;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {
    @GetMapping("/getUsername")
    public Result getUsername(){
        //SecurityContextHolder持有整个Security的所有信息,(配置,登入的权限)
        //getAuthentication获取认证信息->登入用户名
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println(username);
        //getPrincipal主角,主事,登入用户
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        System.out.println(user.getUsername());
        return new Result(true, MessageConstant.GET_USERNAME_SUCCESS,username);
    }
}

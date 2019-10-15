package com.itheima.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.constant.MessageConstant;
import com.itheima.constant.RedisMessageConstant;
import com.itheima.entity.Result;
import com.itheima.pojo.Member;
import com.itheima.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.Map;
@RestController
@RequestMapping("/login")
public class LoginController {
    @Autowired
    private JedisPool jedisPool;
    @Reference
    private MemberService memberService;
    @PostMapping("/check")
    public Result check (@RequestBody Map<String,String> map, HttpServletResponse res){
     //验证验证码
        Jedis jedis = jedisPool.getResource();
        //验证验证码有效性
        String telephone = map.get("telephone");
        String key ="login_"+ RedisMessageConstant.SENDTYPE_LOGIN + "_" + telephone;
        //redis中的验证码
        String codeInRedis = jedis.get(key);
        if (StringUtils.isEmpty(codeInRedis)) {
            //空值,过时或没有点击获取验证码
            return new Result(false,"请点击[获取验证码]");
        }
       //编码规范,先把不符合条件的先处理,后再处理符合条件.
        //有值
        if (!codeInRedis.equals(map.get("validateCode"))) {
          return new Result(false, MessageConstant.VALIDATECODE_ERROR);
        }
        //清除redis中的验证码
        jedis.del(key);
        //判断会员是否存在
       Member member= memberService.findByTelephone(telephone);
        if (null==member) {
         member=new Member();
         member.setPhoneNumber(telephone);
         member.setRegTime(new Date());
         memberService.add(member);
        }
        Cookie cookie=new Cookie("login_member",telephone);
        cookie.setMaxAge(60*60*24*30);//有效一个月
        cookie.setPath("/");//访问那个网页时会带入这个cookie /根目录,所有
        res.addCookie(cookie);
        return new Result(true,MessageConstant.LOGIN_SUCCESS);
    }
}

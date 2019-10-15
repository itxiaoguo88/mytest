package com.itheima.controller;


import com.aliyuncs.exceptions.ClientException;
import com.itheima.constant.MessageConstant;
import com.itheima.constant.RedisMessageConstant;
import com.itheima.entity.Result;
import com.itheima.util.SMSUtils;
import com.itheima.util.ValidateCodeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@RestController
@RequestMapping("/validateCode")
public class ValidateCodeController {
    @Autowired
    private JedisPool jedisPool;
    @PostMapping("/send4Order")
    public Result send4Order(String telephone){
        Jedis jedis = jedisPool.getResource();
        //验证码是否已经发送过了,提示注意查询 ,加上前缀是为了区分业务
       String key= RedisMessageConstant.SENDTYPE_GETPWD+"_"+telephone;
        if (null !=jedis.get(key)) {
            //验证码已经发送过了,提示注意查询
            return new Result(false, MessageConstant.SENT_VALIDATECODE);
        }
        //没有发送过,生成验证码.调用短信工具发送验证码
            Integer code = ValidateCodeUtils.generateValidateCode(6);
        try {
            SMSUtils.sendShortMessage(SMSUtils.VALIDATE_CODE,telephone,code+"");
            //把验证码存入redis中(提交预约时要来做验证,判断是否重复发送)
            //setex 设置的key ,超时会自动删除
            //1,key 2.时间长度.s 3,value
            jedis.setex(key,300,code+"");
            //存入redis中的验证码要设置它的有效期(过期类就会从redis中删除),防止验证码的重复使用,占用内存
            return new Result(true,MessageConstant.SEND_VALIDATECODE_SUCCESS);
        } catch (ClientException e) {
            e.printStackTrace();
        }
      return new Result(false,MessageConstant.SEND_VALIDATECODE_FAIL);
    }

    /**
     * 用于快速登入的
     * @param telephone
     * @return
     */
    @PostMapping("/send4Login")
    public Result send4Login(String telephone){
        Jedis jedis = jedisPool.getResource();
        //验证码是否已经发送过了,提示注意查询 ,加上前缀是为了区分业务
        String key="login_" + RedisMessageConstant.SENDTYPE_LOGIN+"_"+telephone;
        if (null !=jedis.get(key)) {
            //验证码已经发送过了,提示注意查询
            return new Result(false, MessageConstant.SENT_VALIDATECODE);
        }
        //没有发送过,生成验证码.调用短信工具发送验证码
        Integer code = ValidateCodeUtils.generateValidateCode(6);
        try {
            SMSUtils.sendShortMessage(SMSUtils.VALIDATE_CODE,telephone,code+"");
            //把验证码存入redis中(提交预约时要来做验证,判断是否重复发送)
            //setex 设置的key ,超时会自动删除
            //1,key 2.时间长度.s 3,value
            jedis.setex(key,300,code+"");
            //存入redis中的验证码要设置它的有效期(过期类就会从redis中删除),防止验证码的重复使用,占用内存
            return new Result(true,MessageConstant.SEND_VALIDATECODE_SUCCESS);
        } catch (ClientException e) {
            e.printStackTrace();
        }
        return new Result(false,MessageConstant.SEND_VALIDATECODE_FAIL);
    }
}

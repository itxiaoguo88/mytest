package com.itheima.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.constant.MessageConstant;
import com.itheima.constant.RedisMessageConstant;
import com.itheima.entity.Result;
import com.itheima.pojo.Order;
import com.itheima.service.OrderService;
import org.apache.zookeeper.data.Id;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.Map;

@RestController
@RequestMapping("/order")
public class OrderController {
    @Reference
    private OrderService orderService;
    @Autowired
    private JedisPool jedisPool;
    @PostMapping("/submit")
    public Result submit(@RequestBody Map<String,String> orderInfo ){
        //手机号码
        String telephone = orderInfo.get("telephone");
        Jedis jedis = jedisPool.getResource();
       //key要与存进去时一致
        String key= RedisMessageConstant.SENDTYPE_GETPWD+"_"+telephone;
        String codeInRedis = jedis.get(key);
        if (null ==codeInRedis) {
            //提示发送验证码
            return new Result(false, MessageConstant.SEND_VALIDATECODE);
        }
        //判断验证码是否相同
        if (!codeInRedis.equals(orderInfo.get("validateCode"))){
            //不同
            return new Result(true,MessageConstant.SEND_VALIDATECODE_FAIL);
        }
        //设置预约类型
        orderInfo.put("orderType", Order.ORDERTYPE_WEIXIN);
       int orderId= orderService.addOrde(orderInfo);
        return new Result(true,MessageConstant.ORDER_SUCCESS, orderId);
    }
     @GetMapping("/findById")
    public Result findById(int id){
  Map<String,Object> orderInfo= orderService.findById(id);
  return new Result(true,MessageConstant.QUERY_ORDER_SUCCESS,orderInfo);
    }
}

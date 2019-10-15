package com.itheima.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.constant.MessageConstant;
import com.itheima.constant.RedisConstant;
import com.itheima.entity.Result;
import com.itheima.pojo.Package;
import com.itheima.service.PackageService;
import com.itheima.util.QiNiuUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import redis.clients.jedis.JedisPool;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/package")
public class PackageController {

    @Reference
    private PackageService packageService;

    @Autowired
    private JedisPool jedisPool;

    /**
     * 图片的上传
     */
    @PostMapping("/upload")
    public Result upload(@RequestParam("imgFile") MultipartFile imgFile){
        // 图片原名称
        String originalFilename = imgFile.getOriginalFilename();
        // 1. 产生唯一key
        UUID uuid = UUID.randomUUID();
        // 文件的扩展名
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String newFileName = uuid.toString() + extension;
        // 2. 调用QiNiuUtil上传图片
        try {
            QiNiuUtil.uploadViaByte(imgFile.getBytes(), newFileName);
            // 保存到redis，所有
            jedisPool.getResource().sadd(RedisConstant.SETMEAL_PIC_RESOURCES, newFileName);
            // 3. 封装返回的结果 picName, domain
            Map<String,String> resultMap = new HashMap<String,String>();
            resultMap.put("picName", newFileName);
            resultMap.put("domain", QiNiuUtil.DOMAIN);
            // 4. 返回结果  {picName: ..., domain:....}
            return new Result(true, MessageConstant.UPLOAD_SUCCESS, resultMap);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 失败的
        return new Result(false, MessageConstant.PIC_UPLOAD_FAIL);
    }
    
    @PostMapping("/add")
    public Result add(@RequestBody Package pkg, Integer[] checkgroupIds){
        // 调用业务服务添加套餐
        packageService.add(pkg, checkgroupIds);
        // 保存图片到redis中, DB集合
        jedisPool.getResource().sadd(RedisConstant.SETMEAL_PIC_DB_RESOURCES, pkg.getImg());
        return new Result(true, MessageConstant.ADD_PACKAGE_SUCCESS);
    }
}

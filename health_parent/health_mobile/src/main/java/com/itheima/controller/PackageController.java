package com.itheima.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.constant.MessageConstant;
import com.itheima.entity.Result;
import com.itheima.pojo.Package;
import com.itheima.service.PackageService;
import com.itheima.util.QiNiuUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/package")
public class PackageController {

    @Reference
    private PackageService packageService;

    @GetMapping("getPackage")
    public Result getPackage(){
        // 查询所有的套餐列表
        List<Package> list = packageService.findAll();
        // 拼接套餐图片的完整路径
        if(null != list){
            // 取出list中的每个元素赋值给pkg变量
            // pkg相当于list中的元素
            // 只要对pkg进行修改，list中的元素也会跟着修改
            /*for (Package pkg : list) {
                pkg.setImg("http://" + QiNiuUtil.DOMAIN + "/" + pkg.getImg());
            }*/
            list.forEach(pkg -> {
                pkg.setImg("http://" + QiNiuUtil.DOMAIN + "/" + pkg.getImg());
            });
        }
        return new Result(true, MessageConstant.QUERY_PACKAGE_SUCCESS, list);
    }

    /**
     * 查询套餐详情
     * @param id
     * @return
     */
    @GetMapping("/findById")
    public Result findById(int id){
        // 调用业务服务查询套餐详情
        Package pkg = packageService.findById(id);
        // 拼接图片完整路径
        pkg.setImg("http://" + QiNiuUtil.DOMAIN + "/" + pkg.getImg());
        return new Result(true, MessageConstant.QUERY_PACKAGE_SUCCESS,pkg);
    }
}

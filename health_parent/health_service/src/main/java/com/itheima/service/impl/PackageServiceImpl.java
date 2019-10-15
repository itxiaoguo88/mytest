package com.itheima.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.itheima.dao.PackageDao;
import com.itheima.pojo.Package;
import com.itheima.service.PackageService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Service(interfaceClass = PackageService.class)
public class PackageServiceImpl implements PackageService {
    @Autowired
    private PackageDao packageDao;

    /**
     * 添加套餐
     * @param pkg
     * @param checkgroupIds
     */
    @Override
    public void add(Package pkg, Integer[] checkgroupIds) {
        // 调用dao插入套餐
        packageDao.add(pkg);
        // 获取套餐的id
        Integer pkgId = pkg.getId();
        if(null != checkgroupIds){
            for (Integer checkgroupId : checkgroupIds) {
                // 添加套餐与检查组的关系
                packageDao.addPackageCheckGroup(pkgId,checkgroupId);
            }
        }
    }

    @Override
    public List<Package> findAll() {
        return packageDao.findAll();
    }

    /**
     * 通过编号查询套餐详情
     * @param id
     * @return
     */
    @Override
    public Package findById(int id) {
        return packageDao.findById(id);
    }
}

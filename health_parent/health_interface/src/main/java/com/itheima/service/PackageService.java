package com.itheima.service;

import com.itheima.pojo.Package;

import java.util.List;

public interface PackageService {
    /**
     * 添加套餐
     * @param pkg
     * @param checkgroupIds
     */
    void add(Package pkg, Integer[] checkgroupIds);

    /**
     * 查询所有套餐数据
     * @return
     */
    List<Package> findAll();

    /**
     * 通过编号查询套餐详情
     * @param id
     * @return
     */
    Package findById(int id);
}

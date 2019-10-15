package com.itheima.dao;

import com.itheima.pojo.Package;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface PackageDao {
    /**
     * 添加套餐
     * @param pkg
     */
    void add(Package pkg);

    /**
     * 添加套餐与检查组的关系
     * @param pkgId
     * @param checkgroupId
     */
    void addPackageCheckGroup(@Param("pkgId") Integer pkgId, @Param("checkgroupId") Integer checkgroupId);

    /**
     * 查询所有
     * @return
     */
    List<Package> findAll();

    /**
     * 通过编号查询套餐详情
     * @param id
     * @return
     */
    Package findById(int id);

    /**
     * 查询套餐占比数据
     * @return
     */

    List<Map<String,Object>> getPackageReport();
}

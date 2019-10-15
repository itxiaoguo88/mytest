package com.itheima.dao;

import com.github.pagehelper.Page;
import com.itheima.pojo.CheckGroup;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CheckGroupDao {
    /**
     * 添加检查组
     * @param checkGroup
     */
    void add(CheckGroup checkGroup);

    /**
     * 设置检查组与检查项的关系
     * @param checkGroupId
     * @param checkitemid
     */
    void addCheckGroupCheckItem(@Param("checkGroupId") Integer checkGroupId, @Param("checkitemid")Integer checkitemid);

    /**
     * 条件查询
     * @param queryString
     * @return
     */
    Page<CheckGroup> findByCondition(String queryString);

    /**
     * 获取选中的检查项编号集合
     * @param id
     * @return
     */
    List<Integer> findCheckItemIdsById(int id);

    CheckGroup findById(int id);

    /**
     * 更新检查组
     * @param checkGroup
     */
    void update(CheckGroup checkGroup);

    /**
     * 删除检查组与检查项的关系
     * @param checkGroupId
     */
    void deleteCheckItemById(Integer checkGroupId);

    /**
     * 查询所有
     * @return
     */
    List<CheckGroup> findAll();
}

package com.itheima.service;

import com.itheima.entity.PageResult;
import com.itheima.entity.QueryPageBean;
import com.itheima.pojo.CheckGroup;

import java.util.List;

public interface CheckGroupService {
    /**
     * 添加检查组
     * @param checkGroup
     * @param checkitemids
     */
    void add(CheckGroup checkGroup, Integer[] checkitemids);

    /**
     * 分页查询
     * @param queryPageBean
     * @return
     */
    PageResult<CheckGroup> findPage(QueryPageBean queryPageBean);

    /**
     * 获取选中的检查项编号集合
     * @param id
     * @return
     */
    List<Integer> findCheckItemIdsById(int id);

    CheckGroup findById(int id);

    /**
     * 修改检查组
     * @param checkGroup
     * @param checkitemIds
     */
    void update(CheckGroup checkGroup, Integer[] checkitemIds);

    /**
     * 查询所有
     * @return
     */
    List<CheckGroup> findAll();
}

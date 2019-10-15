package com.itheima.dao;

import com.github.pagehelper.Page;
import com.itheima.pojo.CheckItem;

import java.util.List;

public interface CheckItemDao {
    /**
     * 添加
     * @param checkItem
     */
    void add(CheckItem checkItem);

    /**
     * 条件分页查询
     * @param queryString
     * @return
     */
    Page<CheckItem> findAllByCondition(String queryString);

    /**
     * 检查项是否被检查组引用了
     * @param id
     * @return
     */
    int findCountByCheckItemId(int id);

    /**
     * 删除
     * @param id
     */
    void deleteById(int id);

    /**
     * 通过编号查询
     * @param id
     * @return
     */
    CheckItem findById(int id);

    /**
     * 修改检查项
     * @param checkItem
     */
    void update(CheckItem checkItem);

    /**
     * 查询所有
     * @return
     */
    List<CheckItem> findAll();
}

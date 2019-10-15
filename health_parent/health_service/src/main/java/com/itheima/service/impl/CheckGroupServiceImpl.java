package com.itheima.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.itheima.dao.CheckGroupDao;
import com.itheima.entity.PageResult;
import com.itheima.entity.QueryPageBean;
import com.itheima.pojo.CheckGroup;
import com.itheima.service.CheckGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service(interfaceClass = CheckGroupService.class)
public class CheckGroupServiceImpl implements CheckGroupService {

    @Autowired
    private CheckGroupDao checkGroupDao;

    /**
     * 添加检查组
     * @param checkGroup
     * @param checkitemids
     */
    @Transactional
    @Override
    public void add(CheckGroup checkGroup, Integer[] checkitemids) {
        // 添加检查组
        checkGroupDao.add(checkGroup);
        // 通过selectKey方式获取新加的检查组的ID
        Integer checkGroupId = checkGroup.getId();
        // 检查组与检查项的关系
        if(null != checkitemids){
            // 遍历添加
            for (Integer checkitemid : checkitemids) {
                // 添加检查组与检查项的关系
                checkGroupDao.addCheckGroupCheckItem(checkGroupId, checkitemid);
            }
        }
    }

    /**
     * 分页查询
     * @param queryPageBean
     * @return
     */
    @Override
    public PageResult<CheckGroup> findPage(QueryPageBean queryPageBean) {
        if(!StringUtils.isEmpty(queryPageBean.getQueryString())){
            // 模糊查询 %
            queryPageBean.setQueryString("%" + queryPageBean.getQueryString()+ "%");
        }
        // 使用PageHelper分页
        PageHelper.startPage(queryPageBean.getCurrentPage(), queryPageBean.getPageSize());
        // 紧接着的查询语句会被分页
        Page<CheckGroup> page = checkGroupDao.findByCondition(queryPageBean.getQueryString());
        // 封装成分页结果再返回
        PageResult<CheckGroup> pageResult = new PageResult<CheckGroup>(page.getTotal(), page.getResult());
        return pageResult;
    }

    /**
     * 获取选中的检查项编号集合
     * @param id
     * @return
     */
    @Override
    public List<Integer> findCheckItemIdsById(int id) {
        return checkGroupDao.findCheckItemIdsById(id);
    }

    @Override
    public CheckGroup findById(int id) {
        return checkGroupDao.findById(id);
    }

    /**
     * 修改检查组
     * @param checkGroup
     * @param checkitemIds
     */
    @Transactional
    @Override
    public void update(CheckGroup checkGroup, Integer[] checkitemIds) {
        // 1. 更新检查组
        checkGroupDao.update(checkGroup);
        // 2. 删除已有关系
        Integer checkGroupId = checkGroup.getId();
        checkGroupDao.deleteCheckItemById(checkGroupId);
        // 3. 建立新关系
        if(null != checkitemIds){
            for (Integer checkitemId : checkitemIds) {
                // 添加新的关系
                checkGroupDao.addCheckGroupCheckItem(checkGroupId, checkitemId);
            }
        }
    }

    /**
     * 查询所有
     * @return
     */
    @Override
    public List<CheckGroup> findAll() {
        return checkGroupDao.findAll();
    }
}

package com.itheima.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.itheima.constant.MessageConstant;
import com.itheima.dao.CheckItemDao;
import com.itheima.entity.PageResult;
import com.itheima.entity.QueryPageBean;
import com.itheima.exception.MyException;
import com.itheima.pojo.CheckItem;
import com.itheima.service.CheckItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import java.util.List;

@Service(interfaceClass = CheckItemService.class)
public class CheckItemServiceImpl implements CheckItemService {

    @Autowired
    private CheckItemDao checkItemDao;

    @Override
    public void add(CheckItem checkItem) {
        checkItemDao.add(checkItem);
    }

    /**
     * 分页查询
     * @param queryPageBean
     * @return
     */
    @Override
    public PageResult<CheckItem> findPage(QueryPageBean queryPageBean) {
        if(!StringUtils.isEmpty(queryPageBean.getQueryString())){
            // 有查询条件，实现模糊查询
            queryPageBean.setQueryString("%" + queryPageBean.getQueryString()+ "%");
        }
        // 使用分页插件
        PageHelper.startPage(queryPageBean.getCurrentPage(), queryPageBean.getPageSize());
        // 查询语句会被分页
        Page<CheckItem> page = checkItemDao.findAllByCondition(queryPageBean.getQueryString());
        // 分页的结果
        PageResult<CheckItem> pageResult = new PageResult<CheckItem>(page.getTotal(), page.getResult());
        return pageResult;
    }

    @Override
    public void deleteById(int id) throws MyException {
        // 检查一下，看检查项是否被检查组引用了
        int count = checkItemDao.findCountByCheckItemId(id);
        if(count > 0){
            // 有引用，不允许删除
            throw new MyException(MessageConstant.DELETE_CHECKITEM_FAIL_USED);
        }
        checkItemDao.deleteById(id);
    }

    /**
     * 通过编号查询
     * @param id
     * @return
     */
    @Override
    public CheckItem findById(int id) {
        return checkItemDao.findById(id);
    }

    /**
     * 修改检查项
     * @param checkItem
     */
    @Override
    public void update(CheckItem checkItem) {
        checkItemDao.update(checkItem);
    }

    @Override
    public List<CheckItem> findAll() {
        return checkItemDao.findAll();
    }
}

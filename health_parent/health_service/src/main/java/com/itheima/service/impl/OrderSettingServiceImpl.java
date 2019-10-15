package com.itheima.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.itheima.dao.OrderSettingDao;
import com.itheima.pojo.OrderSetting;
import com.itheima.service.OrderSettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service(interfaceClass = OrderSettingService.class)
public class OrderSettingServiceImpl implements OrderSettingService {

    @Autowired
    private OrderSettingDao orderSettingDao;

    /**
     * 批量导入
     * @param list
     */
    @Transactional
    @Override
    public void doImport(List<OrderSetting> list) {
        // 1. 遍历list,
        for (OrderSetting orderSetting : list) {
            // 2. 调用dao按日期查询看是否存在
            OrderSetting osInDB = orderSettingDao.findByOrderDate(orderSetting.getOrderDate());
            if(null != osInDB){
                // 3. 如果存在则更新数量
                osInDB.setNumber(orderSetting.getNumber());
                orderSettingDao.updateNumber(osInDB);
            }else {
                // 4. 不存在则插入
                orderSettingDao.add(orderSetting);
            }
        }
    }

    @Override
    public List<OrderSetting> getOrderSettingByMonth(String month) {
        // month = 2019-09
        // 按时间范围查询
        String startDate = month + "-01";
        String endDate = month + "-31";
        // 调用dao范围查询
        return orderSettingDao.getOrderSettingBetweenDate(startDate, endDate);
    }

    /**
     * 通过日期设置预约人数
     * @param orderSetting
     */
    @Override
    public void editNumberByDate(OrderSetting orderSetting) {
        // 判断是否已经存在数据
        OrderSetting os = orderSettingDao.findByOrderDate(orderSetting.getOrderDate());
        if(null == os){
            // 不存在
            orderSettingDao.add(orderSetting);
        }else{
            // 设置更新后的值
            os.setNumber(orderSetting.getNumber());
            orderSettingDao.updateNumber(os);
        }
    }
}

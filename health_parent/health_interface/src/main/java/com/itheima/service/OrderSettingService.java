package com.itheima.service;

import com.itheima.pojo.OrderSetting;

import java.util.List;

public interface OrderSettingService {
    /**
     * 批量导入
     * @param list
     */
    void doImport(List<OrderSetting> list);

    List<OrderSetting> getOrderSettingByMonth(String month);

    /**
     * 通过日期设置预约人数
     * @param orderSetting
     */
    void editNumberByDate(OrderSetting orderSetting);

}

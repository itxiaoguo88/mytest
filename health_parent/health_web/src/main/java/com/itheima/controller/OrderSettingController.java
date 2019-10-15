package com.itheima.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.constant.MessageConstant;
import com.itheima.entity.Result;
import com.itheima.pojo.OrderSetting;
import com.itheima.service.OrderSettingService;
import com.itheima.util.POIUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("/ordersetting")
public class OrderSettingController {

    @Reference
    private OrderSettingService orderSettingService;

    @PostMapping("/upload")
    public Result upload(@RequestParam("excelFile") MultipartFile excelFile){
        //解析excel，
        try {
            List<String[]> strings = POIUtils.readExcel(excelFile);
            // 把它转成OrderSetting实体List，
            List<OrderSetting> list = new ArrayList<>();
            OrderSetting os = null;
            // 日期转化
            SimpleDateFormat sdf = new SimpleDateFormat(POIUtils.DATE_FORMAT);
            for (String[] arr : strings) {
                //0:日期	1:可预约数量
                os = new OrderSetting(sdf.parse(arr[0]), Integer.valueOf(arr[1]));
                list.add(os);
            }
            // 再调用业务服务
            if(list.size() > 0){
                orderSettingService.doImport(list);
                return new Result(true, MessageConstant.IMPORT_ORDERSETTING_SUCCESS);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Result(false, MessageConstant.IMPORT_ORDERSETTING_FAIL);
    }

    @PostMapping("/getOrderSettingByMonth")
    public Result getOrderSettingByMonth(String month){
        // 调用业务服务查询
        List<OrderSetting> list = orderSettingService.getOrderSettingByMonth(month);
        // 转成leftobj的格式 { date: 6, number: 120, reservations: 1 } => map
        // [{key:{key2:[{},{key3:[]}]}}] => List<Map<key,Map<ke2,List<Map<key3,List>>>>>
        // 返回的结果集
        List<Map<String,Object>> resultList = new ArrayList<Map<String,Object>>();
        // 每日的数据
        Map<String,Object> dayData = null;
        // 数据格式转化
        if(null != list && list.size() > 0){
            SimpleDateFormat sdf = new SimpleDateFormat("d");
            for (OrderSetting os : list) {
                dayData = new HashMap<>();
                dayData.put("number",os.getNumber());
                dayData.put("reservations",os.getReservations());
                // Integer.valueOf(sdf.format(os.getOrderDate())) 获取日期中的天数，转成整型
                dayData.put("date",Integer.valueOf(sdf.format(os.getOrderDate())));
                // 放入返回的结果集
                resultList.add(dayData);
            }
        }
        return new Result(true, MessageConstant.GET_ORDERSETTING_SUCCESS, resultList);
    }

    @PostMapping("/editNumberByDate")
    public Result editNumberByDate(@RequestBody OrderSetting orderSetting){
        // 调用业务
        orderSettingService.editNumberByDate(orderSetting);
        return new Result(true, MessageConstant.ORDERSETTING_SUCCESS);
    }

}

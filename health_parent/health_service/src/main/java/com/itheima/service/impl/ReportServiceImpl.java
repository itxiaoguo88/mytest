package com.itheima.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.itheima.constant.MessageConstant;
import com.itheima.dao.MemberDao;
import com.itheima.dao.OrderDao;
import com.itheima.dao.PackageDao;
import com.itheima.exception.MyException;
import com.itheima.service.ReportService;
import com.itheima.util.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.SimpleDateFormat;
import java.util.*;

@Service(interfaceClass = ReportService.class)
public class ReportServiceImpl implements ReportService {
    @Autowired
   private PackageDao packageDao;
    @Autowired
    private MemberDao memberDao;
    @Autowired
   private OrderDao orderDao;
    @Override
    public List<Map<String, Object>> getPackageReport() {
        return packageDao.getPackageReport();
    }

    @Override
    public Map<String, Object> getBusinessReportData() {
        //获取当前日期
        Date date = new Date();
        //把日期转成字符串日期
        String reportDate = DateUtils.date2String(date,DateUtils.YMD);
        //获取本周星期一
        String monday = DateUtils.date2String(DateUtils.getThisWeekMonday(), DateUtils.YMD);
        //获取本周星期天
        String sunday = DateUtils.date2String(DateUtils.getSundayOfThisWeek(), DateUtils.YMD);
        //获取本月第一天
        String firstDayOfThisMonth = DateUtils.date2String(DateUtils.getFirstDayOfThisMonth(), DateUtils.YMD);
        //获取本月最后一天
        String lastDayOfThisMonth = DateUtils.date2String(DateUtils.getLastDayOfThisMonth(), DateUtils.YMD);
        //******************************会员数据统计*********************************
        //今日新增会员数
        Integer todayNewMember = memberDao.findMemberCountByDate(reportDate);
        //会员总数
        Integer totalMember = memberDao.findMemberTotalCount();
        //本周新增会员数
        Integer thisWeekNewMember = memberDao.findMemberCountAfterDate(monday);
        //本月新增会员数
        Integer thisMonthNewMember = memberDao.findMemberCountAfterDate(firstDayOfThisMonth);

   //***************************预计到诊数据统计*******************************
        //今日预约人数
        Integer todayOrderNumber = orderDao.findOrderCountByDate(reportDate);
        //今日到诊人数
        Integer todayVisitsNumber = orderDao.findVisitsCountByDate(reportDate);
        //本周预约人数
       Integer thisWeekOrderNumber= orderDao.findOrderCountBetweenDate(monday,sunday);
       //本周到诊人数
       Integer thisWeekVisitsNumber =orderDao.findVisitsCountAfterDate(monday);
       //本月预约人数
       Integer thisMonthOrderNumber = orderDao.findOrderCountBetweenDate(firstDayOfThisMonth,lastDayOfThisMonth);
       //本月到诊人数
        Integer thisMonthVisitsNumber=orderDao.findVisitsCountAfterDate(firstDayOfThisMonth);
        //************************热门套餐列表****************************
        List<Map<String,Object>> hotPackage=orderDao.findHotPackage();

        Map<String, Object> result=new HashMap<>();
        result.put("reportDate",reportDate);
        result.put("todayNewMember",todayNewMember);
        result.put("totalMember",totalMember);
        result.put("thisWeekNewMember",thisWeekNewMember);
        result.put("thisMonthNewMember",thisMonthNewMember);
        result.put("todayOrderNumber",todayOrderNumber);
        result.put("todayVisitsNumber",todayVisitsNumber);
        result.put("thisWeekOrderNumber",thisWeekOrderNumber);
        result.put("thisWeekVisitsNumber",thisWeekVisitsNumber);
        result.put("thisMonthOrderNumber",thisMonthOrderNumber);
        result.put("thisMonthVisitsNumber",thisMonthVisitsNumber);
        result.put("hotPackage",hotPackage);
        return result;
    }

    @Override
    public Map<String, List<Object>> getByDateMemberReport(String startTime, String finishTime) {
        ArrayList<String> results = new ArrayList<String>();
       SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");//格式化为年月

       Calendar min = Calendar.getInstance();
       Calendar max = Calendar.getInstance();

        try {
            min.setTime(sdf.parse(startTime));
            min.set(min.get(Calendar.YEAR), min.get(Calendar.MONTH), 1);

            max.setTime(sdf.parse(finishTime));
            max.set(max.get(Calendar.YEAR), max.get(Calendar.MONTH), 2);

            Calendar curr = min;
            while (curr.before(max)) {
              results.add(sdf.format(curr.getTime()));
                curr.add(Calendar.MONTH, 1);
               }
            //遍历出每个月,每个月要查一次
            List<Object>months=new ArrayList<>();
            //返回数量的集合
            List<Object>memberCount=new ArrayList<>();
            for (String s : results) {
                months.add(s);
                //获取本月第一天
                String firstDayOfThisMonth = s+"-01";
                //获取本月最后一天
                String lastDayOfThisMonth = s+"-31";
                //本月会员总数
                Integer monthCount = memberDao.getByDateMemberReport(firstDayOfThisMonth,lastDayOfThisMonth);
                memberCount.add(monthCount);
            }


            //3.查询到的数据封装到 months list月份, memberCount list 到这个月份为止的会员总数量
        Map<String,List<Object>> result =new HashMap<>();
        result.put("months",months);
        result.put("memberCount",memberCount);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
           throw new MyException(MessageConstant.GET_MEMBER_NUMBER_REPORT_SUCCESS);
        }
    }
//        return memberDao.getByDateMemberReport(startTime,finishTime);
//    }
}

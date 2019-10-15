package com.itheima.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.itheima.constant.MessageConstant;
import com.itheima.dao.MemberDao;
import com.itheima.dao.OrderDao;
import com.itheima.dao.OrderSettingDao;
import com.itheima.exception.MyException;
import com.itheima.pojo.Member;
import com.itheima.pojo.Order;
import com.itheima.pojo.OrderSetting;
import com.itheima.service.OrderService;
import com.itheima.util.DateUtils;
import com.sun.org.apache.bcel.internal.generic.FMUL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
@Service(interfaceClass = OrderService.class)
public class OrderServiceImpl implements OrderService {
    @Autowired
    private OrderSettingDao orderSettingDao;
    @Autowired
    private MemberDao memberDao;
    @Autowired
    private OrderDao orderDao;
    @Override
    @Transactional
    public int addOrde(Map<String, String> orderInfo) throws MyException{
     //1.是否可以预约,通过预约日期调用dao查询t_ordersetting
        String orderDate = orderInfo.get("orderDate");
        OrderSetting orderSetting= orderSettingDao.findOrderByDate(orderDate);
        //1.1返回空值不能预约
        if (null==orderSetting){
            throw new MyException(MessageConstant.SELECTED_DATE_CANNOT_ORDER);
        }
     //1.2有记录:已经预约人数是否>=可预约人数 报错
        if(orderSetting.getReservations()>=orderSetting.getNumber()){
           throw new MyException(MessageConstant.ORDER_FULL);
        }
     //2判断是否为会员,通过手机号码查询
        String telephone = orderInfo.get("telephone");
      Member member= memberDao.findByTelephone(telephone);
        if (null==member){
     //2.2不存在,调用dao插入新会员t_member,获取id
            member=new Member();
            member.setRegTime(new Date());
            member.setPhoneNumber(telephone);
            member.setIdCard(orderInfo.get("idCard"));
            member.setName(orderInfo.get("name"));
            member.setSex(orderInfo.get("sex"));
            memberDao.add(member);
        }
            //2.1存在取出会员id
        Integer memberId = member.getId();
        //3.判断是否重复预约,通过日期和会员的编号查询t_order
        Order order = new Order();
        order.setMemberId(memberId);
        try {
            order.setOrderDate(DateUtils.parseString2Date(orderDate));
        } catch (Exception e) {
            e.printStackTrace();
            throw new MyException(MessageConstant.ORDERSETTING_FAIL);
        }
        List<Order> ordres = orderDao.findByCondition(order);
        //3.1存在则报重复,报错
        if (null!=ordres && ordres.size()>0){
            throw new MyException(MessageConstant.HAS_ORDERED);
        }
     //3.2不存在则插入t_order,订单,会员id 套餐id 预约日期
        order.setPackageId(Integer.valueOf(orderInfo.get("packageId")));
     //orderstatus:未到诊
        order.setOrderStatus(Order.ORDERSTATUS_NO);
        order.setOrderType(orderInfo.get("orderType"));
        orderDao.add(order);
        //增加已预约的人数
        orderSettingDao.editReservationsByOrderDate(1,orderDate);
        return order.getId();
    }

    @Override
    public Map<String, Object> findById(int id) {
        Map<String, Object> map = orderDao.findById(id);
        Object orderDate = map.get("orderDate");
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String format = dateFormat.format(orderDate);
        map.put("orderDate", format);
        return map;

    }

}

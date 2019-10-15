package com.itheima.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.itheima.dao.MemberDao;
import com.itheima.pojo.Member;
import com.itheima.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class MemberServiceImpl implements MemberService {
    @Autowired
    private MemberDao memberDao;
    @Override
    public Member findByTelephone(String telephone) {
        return memberDao.findByTelephone(telephone);
    }

    @Override
    public void add(Member member) {
      memberDao.add(member);
    }

    /**
     * 会员统计
     * @return
     */
    @Override
    public Map<String, List<Object>> getMemberReport() {
        List<Map<String,Object>>list=new ArrayList<Map<String,Object>>(12);
        //1.获取上一年分的数据
        //日历对象,java来操作日期时间,当前系统时间
        Calendar calendar = Calendar.getInstance();
        //回到12个月以前的数据
        calendar.add(Calendar.MONTH,-12);
        //2.循环12个月,每个月要查一次
        List<Object>months=new ArrayList<>();
        //返回数量的集合
        List<Object>memberCount=new ArrayList<>();
        SimpleDateFormat srf=new SimpleDateFormat("yyyy-MM");
        for (int i=1;i<=12;i++){
            //计算当前月的值
            calendar.add(Calendar.MONTH,1);
            //月份的字符串 2019-01
            String monthStr = srf.format(calendar.getTime());
             months.add(monthStr);

             //查询
            Integer monthCount = memberDao.findMemberCountBeforeDate(monthStr + "-31");
            memberCount.add(monthCount);
        }
        //3.查询到的数据封装到 months list月份, memberCount list 到这个月份为止的会员总数量
            Map<String,List<Object>> result =new HashMap<>();
            result.put("months",months);
            result.put("memberCount",memberCount);
        return result;
    }
}

package com.itheima.service;

import com.itheima.pojo.Member;

import java.util.List;
import java.util.Map;

public interface MemberService {
    Member findByTelephone(String telephone);

    void add(Member member);

    /**
     * 会员统计
     * @return
     */
    Map<String,List<Object>> getMemberReport();
}

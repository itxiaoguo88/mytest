package com.itheima.service;

import com.itheima.exception.MyException;

import java.util.Map;

public interface OrderService {
    int addOrde (Map<String, String> orderInfo)throws MyException;

    Map<String,Object> findById(int id);
}

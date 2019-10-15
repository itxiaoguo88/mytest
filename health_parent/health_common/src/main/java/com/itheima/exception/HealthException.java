package com.itheima.exception;

/**
 * 自定义异常：
 * 使用场景：终止已经不符合业务逻辑的代码继续执行
 */
public class HealthException extends RuntimeException {

    //private static final long serialVersionUID = 1127151684998116941L;

    public HealthException(String message){
        super(message);
    }
}

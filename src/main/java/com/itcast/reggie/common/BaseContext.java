package com.itcast.reggie.common;


/**
 * 基于封装工具类，用户保存和获取当前登录的id
 */
public class BaseContext {
    private  static ThreadLocal<Long> threadLocal=new ThreadLocal<>();

    public  static void  setCurrentId(Long id){
        threadLocal.set(id);
    }
    public  static  Long getCurrentId(){
        return  threadLocal.get();
    }
}

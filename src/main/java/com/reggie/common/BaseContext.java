package com.reggie.common;

/**
 * 基于ThreadLocal封装的工具类，用于保存用户和获取当前登录ID
 * (作用域是整个线程，线程执行流程-》 loginCheckFilter -> EmployeeController -> MyMetaObjectHandler)
 */
public class BaseContext extends ThreadLocal {

    private static ThreadLocal<Long> threadLocal = new ThreadLocal<Long>();

    /**
     * 设置值
     * @param id
     */
    public static void setCurrentId(Long id) {
        threadLocal.set(id);
    }

    /**
     * 获取值
     * @return
     */
    public static Long getCurrentId(){
        return threadLocal.get();
    }
}

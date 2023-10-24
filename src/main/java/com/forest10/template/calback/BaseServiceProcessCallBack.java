package com.forest10.template.calback;


import com.forest10.template.common.ServiceFallBackEnum;

/**
 * @author Forest10
 * @date 2022/4/24 15:31
 */
public abstract class BaseServiceProcessCallBack<T> {


    /**
     * 这个跟checkParams不同的是有的参数检测是不依赖preInit的
     */
    public void checkParamsIgnoreInit() {

    }

    /**
     * 预先初始化
     */
    public void preInit() {

    }

    /**
     * 参数检查
     */
    public void checkParams() {
    }


    /**
     * 执行待处理操作，比如模型的创建，修改，删除等
     *
     * @return T
     */
    public abstract T process();

    public void successProcess() {
    }


    public ServiceFallBackEnum getFallBackType() {
        return ServiceFallBackEnum.NONE;
    }

    /**
     * 执行catch处理操作，用于process逻辑补偿.优雅返回default逻辑
     *
     * @return T
     */
    public T fallBackProcess() {
        return null;
    }

    /**
     * 执行catch处理操作，用于process e逻辑补偿
     *
     * @return T
     */
    public void fallBackProcess(Exception e) {

    }


    /**
     * 执行成功的监控
     *
     * @param execTime 执行时长
     */
    public void succMonitor(long execTime) {
    }

    /**
     * 执行失败的监控
     */
    public void failMonitor() {
    }

    /**
     * finally中调用方法
     */
    public void afterProcess() {
    }

    /**
     * 获取监控名称
     */
    public String getMonitorKeyName() {
        return "";
    }
}

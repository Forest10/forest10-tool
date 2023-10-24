package com.forest10.template.calback;


/**
 * @author Forest10
 * @date 2022/4/24 15:32
 */
public abstract class BaseServiceProcessCallBackNoResult extends BaseServiceProcessCallBack<Void> {

    @Override
    public Void process() {
        processNoResult();
        return null;
    }

    protected abstract void processNoResult();
}

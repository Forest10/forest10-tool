package com.forest10.template.calback;

/**
 * 业务处理回调
 *
 * @author Forest10
 * @date 2018/9/12 下午5:26
 */
public abstract class BaseServiceProcessCallBack<T> {

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

}

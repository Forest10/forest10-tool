package com.forest10.template;

import com.forest10.exception.BizException;
import com.forest10.exception.UnknowException;
import com.forest10.template.calback.BaseServiceProcessCallBack;
import com.forest10.template.calback.BaseServiceProcessCallBackNoResult;
import lombok.extern.slf4j.Slf4j;

/**
 * 业务处理模板
 *
 * @author Forest10
 * @date 2018/9/12 下午5:26
 */
@Slf4j
public class ServiceHandleTemplate {

	private ServiceHandleTemplate() {
	}

	/**
	 * 没有result的模板方法
	 *
	 * @param action 操作回调接口
	 */
	public static void executeNoResult(BaseServiceProcessCallBackNoResult action) {
		execute(action);
	}

	/**
	 * 有result的模板方法
	 *
	 * @param action 操作回调接口
	 */
	public static <T> T execute(BaseServiceProcessCallBack<T> action) {

		T result;

		long startTime = System.currentTimeMillis();

		try {
			// 参数校验
			{
				action.checkParams();
			}
			// 执行业务操作
			{
				result = action.process();
			}
			// 监控成功结果
			{
				action.succMonitor(System.currentTimeMillis() - startTime);
			}
		} catch (BizException e) {
			// 监控失败结果
			{
				action.failMonitor();
			}
			//后期可以增加监控项目

			log.error("系统异常! {}", e.getMessage());
			throw e;
		} catch (Exception e) {
			// 监控失败结果
			{
				action.failMonitor();
			}

			//后期可以增加监控项目
			log.error("系统未知异常! {}", e.getMessage());
			throw new UnknowException(e);
		} finally {
			try {
				{
					action.afterProcess();
				}
			} catch (Exception e) {
				log.error("finally中调用方法出现异常！e:" + e.getMessage(), e);
			}

		}
		return result;
	}

}

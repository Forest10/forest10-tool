package com.forest10.template;


import com.forest10.template.calback.BaseServiceProcessCallBack;
import com.forest10.template.calback.BaseServiceProcessCallBackNoResult;
import com.forest10.template.common.ServiceFallBackEnum;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

/**
 * @author Forest10
 * @date 2022/4/24 15:31
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

		T result = null;

		long startTime = System.currentTimeMillis();
		String monitorKeyName = action.getMonitorKeyName();
		try {
			{
				action.checkParamsIgnoreInit();
			}
			{
				action.preInit();
			}
			// 参数校验
			{
				action.checkParams();
			}
			// 执行业务操作
			{
				result = action.process();
			}

			{
				action.successProcess();
			}
			// 监控成功结果
			{
				action.succMonitor(System.currentTimeMillis() - startTime);
			}
		} catch (Exception e) {
			// 监控失败结果
			{
				action.failMonitor();
			}
			//后期可以增加监控项目
			log.error("系统异常 ", e);

			ServiceFallBackEnum serviceFallBackEnum = Optional.ofNullable(action.getFallBackType())
					.orElse(ServiceFallBackEnum.NONE);

			if (ServiceFallBackEnum.NONE.equals(serviceFallBackEnum)) {
				throw e;
			}
			if (ServiceFallBackEnum.EXEC_FALLBACK.equals(serviceFallBackEnum)) {
				result = action.fallBackProcess();
			}
			if (ServiceFallBackEnum.HANDLE_EXCEPTION.equals(serviceFallBackEnum)) {
				action.fallBackProcess(e);
			}
			if (ServiceFallBackEnum.HANDLE_EXCEPTION_AND_THROW.equals(serviceFallBackEnum)) {
				action.fallBackProcess(e);
				throw e;
			}

		} finally {
			try {
				{
					action.afterProcess();
				}
			} catch (Exception e) {
				log.error("finally中调用方法出现异常-{}", e.getMessage());
			}

		}
		return result;
	}

}
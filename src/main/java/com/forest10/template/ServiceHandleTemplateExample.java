package com.forest10.template;

import com.forest10.template.calback.BaseServiceProcessCallBack;
import com.forest10.template.calback.BaseServiceProcessCallBackNoResult;
import com.forest10.template.common.ServiceFallBackEnum;

/**
 * @author Forest10
 * @date 2023/10/24 12:05
 */
public class ServiceHandleTemplateExample {

	public static void main(String[] args) {
		int execFallback = EXEC_FALLBACK();
		System.out.println(execFallback);
		HANDLE_EXCEPTION();
	}


	public static void HANDLE_EXCEPTION() {
		ServiceHandleTemplate.executeNoResult(new BaseServiceProcessCallBackNoResult() {
			@Override
			protected void processNoResult() {
				int i = 1 / 0;
			}

			@Override
			public ServiceFallBackEnum getFallBackType() {
				return ServiceFallBackEnum.HANDLE_EXCEPTION;
			}

			@Override
			public void fallBackProcess(Exception e) {
				System.out.println("fallBackProcess");
			}

			@Override
			public void successProcess() {
				System.out.println("successProcess");

			}
		});
	}

	public static int EXEC_FALLBACK() {
		return ServiceHandleTemplate.execute(new BaseServiceProcessCallBack<Integer>() {
			@Override
			public Integer process() {
				return 1 / 0;
			}

			@Override
			public ServiceFallBackEnum getFallBackType() {
				return ServiceFallBackEnum.EXEC_FALLBACK;
			}

			@Override
			public Integer fallBackProcess() {
				System.out.println("走到这里说明服务降级了");
				return 2;
			}
		});
	}
}

package org.osidocker.open.api.test;

import org.osidocker.open.web.executor.AsyncTaskService;
import org.osidocker.open.web.executor.DemoTaskExecutorConfig;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class RunAsyncService {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		AnnotationConfigApplicationContext acac = new AnnotationConfigApplicationContext(DemoTaskExecutorConfig.class);
		AsyncTaskService ats = acac.getBean(AsyncTaskService.class);
		for (int i = 0; i < 10; i ++) {
			ats.executeAsyncTask(i);
		}
	}

}

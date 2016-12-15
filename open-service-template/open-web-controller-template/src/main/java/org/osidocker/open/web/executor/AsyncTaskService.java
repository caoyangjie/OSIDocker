package org.osidocker.open.web.executor;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@Async//在类上表名这个类的方法全部都是异步并发执行
public class AsyncTaskService {
	
	@Async//表名要执行异步方法
	public void executeAsyncTask(Integer i){
		System.out.println(i);
	}
}

package org.osidocker.open.api.test;

import org.osidocker.open.web.condition.ConditionConfig;
import org.osidocker.open.web.condition.ListService;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class RunConditionService {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(ConditionConfig.class);
		ListService serv = ctx.getBean(ListService.class);
		System.out.println(ctx.getEnvironment().getProperty("os.name")+"系统下的命令为："+serv.ShowListCMD());
	}

}

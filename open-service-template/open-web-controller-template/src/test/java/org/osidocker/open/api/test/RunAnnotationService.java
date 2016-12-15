package org.osidocker.open.api.test;

import org.osidocker.open.web.annotation.DemoService;
import org.osidocker.open.web.annotation.DemoServiceConfig;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class RunAnnotationService {
	
	public static void main(String[] args){
		AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(DemoServiceConfig.class);
		DemoService ds = ctx.getBean(DemoService.class);
		ds.outputResult();
	}
}

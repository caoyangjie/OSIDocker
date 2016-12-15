package org.osidocker.open.api.test;

import org.osidocker.open.web.scheduled.TaskSchedulerConfig;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class RunScheduleService {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(TaskSchedulerConfig.class);
	}

}

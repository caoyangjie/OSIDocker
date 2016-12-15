package org.osidocker.open.web.scheduled;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class DemoScheduleTaskService {
	
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
	
	@Scheduled(fixedRate=5000)
	public void reportCurrentTime(){
		System.out.println("每隔多久执行一次"+dateFormat.format(new Date()));
	}
	
	@Scheduled(cron="0 53 11 ? * *")
	public void fixTimeExecution(){
		System.out.println("在指定时间"+dateFormat.format(new Date())+"执行任务");
	}
}

package org.osidocker.open.web.scheduled;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@ComponentScan("org.osidocker.open.web.scheduled")
@EnableScheduling
public class TaskSchedulerConfig {

}

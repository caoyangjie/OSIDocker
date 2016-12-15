package org.osidocker.open.web.annotation;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@EnableScheduling//开启计划任务
@EnableAsync
@EnableAspectJAutoProxy//对aspectj自动代理
@EnableAutoConfiguration//
@EnableCaching//开启缓存
@EnableConfigurationProperties
@EnableJpaRepositories//开启jpa
@EnableWebMvc//开启MVC
@EnableTransactionManagement//开启事务
public @interface SpringAnnotationSupport {

}

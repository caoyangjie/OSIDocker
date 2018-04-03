package com.osidocker.open.micro.listeners;

import com.osidocker.open.micro.rebuild.RebuildCacheThread;
import com.osidocker.open.micro.spring.SpringContext;
import com.osidocker.open.micro.threadpool.MicroRequestProcessorThreadPool;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class InitListener implements ServletContextListener{

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        //初始化工作线程池和内存队列
//        MicroRequestProcessorThreadPool.getInstance();
        //设置SpringContent上下文对象
        initSpringContext(sce);
        //启动kafka消费者线程
//        new Thread(new KafkaConsumer("cache-message")).start();
        //启动缓存重建线程
//        new Thread(new RebuildCacheThread()).start();
        //获取ZookeeperSession对象
//        ZooKeeperSession.getInstance();
    }

    private void initSpringContext(ServletContextEvent sce) {
        ServletContext sc = sce.getServletContext();
        ApplicationContext context = WebApplicationContextUtils.getWebApplicationContext(sc);
        SpringContext.setApplicationContext(context);
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        System.out.println("=============== 系统销毁Listener");
    }

}

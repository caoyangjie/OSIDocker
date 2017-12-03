package com.osidocker.open.micro.listeners;

import com.osidocker.open.micro.threadpool.MicroRequestProcessorThreadPool;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class InitListener implements ServletContextListener{

    public void contextInitialized(ServletContextEvent servletContextEvent) {
        //初始化工作线程池和内存队列
        MicroRequestProcessorThreadPool.getInstance();
    }

    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        System.out.println("=============== 系统销毁Listener");
    }

}

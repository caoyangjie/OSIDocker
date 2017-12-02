package com.osidocker.open.micro.listeners;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class InitListener implements ServletContextListener{

    public void contextInitialized(ServletContextEvent servletContextEvent) {
        System.out.println("=============== 系统执行初始化启动listener");
    }

    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        System.out.println("=============== 系统销毁Listener");
    }

}

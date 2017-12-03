package com.osidocker.open.micro.config;

import com.osidocker.open.micro.listeners.InitListener;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 用来作为系统启动执行的第三方插件注入类
 *
 * @author Administrator
 * @creato 2017-12-02 14:26
 */
@Configuration
public class InitContentConfig {

    @Bean
    public ServletListenerRegistrationBean initListenerBean(){
        ServletListenerRegistrationBean reg = new ServletListenerRegistrationBean();
        reg.setListener(new InitListener());
        return reg;
    }

}

package com.osidocker.open.micro.utils;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author haha
 * @date 2020/03/21
 * apollo组件注入
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "apollo.open")
public class ApolloOpen {
    private String appId;
    /**
     * apollo portal地址信息
     */
    private String portal;
    /**
     * 授权token
     */
    private String token;
    /**
     * apollo环境信息
     */
    private String env;
    /**
     * apollo集群信息
     */
    private String clusterName;

    /**
     * 操作发布者
     */
    private String releaseAuthor;
}

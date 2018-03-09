/**
 * ===================================================================================
 * <p>
 * <p>
 * <p>
 * <p>
 * ===================================================================================
 */
package com.osidocker.open.micro.config;

import com.osidocker.open.micro.spring.SpringContext;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @公司名称： 深圳原形信息技术有限公司
 * @类功能说明：
 * @类修改者： 曹杨杰
 * @author: Administrator
 * @创建日期： 创建于18:25 2018/3/8
 * @修改说明：
 * @修改日期： 修改于18:25 2018/3/8
 * @版本号： V1.0.0
 */
@Component(HystrixCommandConfig.HYSTRIX_CONFIG)
@ConfigurationProperties(prefix = "command.hystrix")
public class HystrixCommandConfig {

    public static final String HYSTRIX_CONFIG = "hystrixConfig";
    /**
     * 功能模块名称
     */
    private String modelName;
    /**
     * 命令名称
     */
    private String commandName;
    /**
     * 线程池名称
     */
    private String poolName;
    /**
     * 最大线程数
     */
    private int coreSize;
    /**
     * 最大等待队列
     */
    private int maxQueueSize;
    /**
     * 排队线程数量阈值
     */
    private int queueSizeRejectionThreshold;
    /**
     * 在{circuitBreakerSleepMilliseconds}请求失败次数后开启断路器
     */
    private int circuitBreakerReuqest;
    /**
     * 请求失败率百分之字段值启动断路器
     */
    private int circuitBreakerErrorPercentage;
    /**
     * 短路多久后开始尝试是否恢复
     */
    private int circuitBreakerSleepMilliseconds;
    /**
     * 请求执行超时时间
     */
    private int timeout;
    /**
     * 调用线程允许并发进行的降级失败处理函数数量
     */
    private int fallbackIsolationSemaphoreMaxRequest;
    /**
     * 多少个请求开启一个批次
     */
    private int maxRequestBatch;
    /**
     * 设置批处理创建到执行之间的毫秒数
     */
    private int timerDelayInMilliseconds;

    public String getModelName() {
        return modelName;
    }

    public String getCommandName() {
        return commandName;
    }

    public String getPoolName() {
        return poolName;
    }

    public int getCoreSize() {
        return coreSize;
    }

    public void setCoreSize(int coreSize) {
        this.coreSize = coreSize;
    }

    public int getMaxQueueSize() {
        return maxQueueSize;
    }

    public void setMaxQueueSize(int maxQueueSize) {
        this.maxQueueSize = maxQueueSize;
    }

    public int getQueueSizeRejectionThreshold() {
        return queueSizeRejectionThreshold;
    }

    public void setQueueSizeRejectionThreshold(int queueSizeRejectionThreshold) {
        this.queueSizeRejectionThreshold = queueSizeRejectionThreshold;
    }

    public int getCircuitBreakerReuqest() {
        return circuitBreakerReuqest;
    }

    public void setCircuitBreakerReuqest(int circuitBreakerReuqest) {
        this.circuitBreakerReuqest = circuitBreakerReuqest;
    }

    public int getCircuitBreakerSleepMilliseconds() {
        return circuitBreakerSleepMilliseconds;
    }

    public void setCircuitBreakerSleepMilliseconds(int circuitBreakerSleepMilliseconds) {
        this.circuitBreakerSleepMilliseconds = circuitBreakerSleepMilliseconds;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public int getFallbackIsolationSemaphoreMaxRequest() {
        return fallbackIsolationSemaphoreMaxRequest;
    }

    public void setFallbackIsolationSemaphoreMaxRequest(int fallbackIsolationSemaphoreMaxRequest) {
        this.fallbackIsolationSemaphoreMaxRequest = fallbackIsolationSemaphoreMaxRequest;
    }

    public int getCircuitBreakerErrorPercentage() {
        return circuitBreakerErrorPercentage;
    }

    public void setCircuitBreakerErrorPercentage(int circuitBreakerErrorPercentage) {
        this.circuitBreakerErrorPercentage = circuitBreakerErrorPercentage;
    }

    public int getMaxRequestBatch() {
        return maxRequestBatch;
    }

    public void setMaxRequestBatch(int maxRequestBatch) {
        this.maxRequestBatch = maxRequestBatch;
    }

    public int getTimerDelayInMilliseconds() {
        return timerDelayInMilliseconds;
    }

    public void setTimerDelayInMilliseconds(int timerDelayInMilliseconds) {
        this.timerDelayInMilliseconds = timerDelayInMilliseconds;
    }

    public static HystrixCommandConfig getInstance(String modelName, String commandName, String poolName){
        HystrixCommandConfig config = (HystrixCommandConfig) SpringContext.getApplicationContext().getBean(HYSTRIX_CONFIG);
        config.commandName = commandName;
        config.modelName = modelName;
        config.poolName = poolName;
        return config;
    }
}

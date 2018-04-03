package com.osidocker.open.micro.hystrix;

import com.netflix.hystrix.*;
import com.osidocker.open.micro.config.HystrixCommandConfig;
import com.osidocker.open.micro.entity.IsDegrade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 获取城市名称的command
 * @author Administrator
 *
 */
public abstract class AbsHystrixCommand<RequestEntity,ResponseEntity> extends HystrixCommand<ResponseEntity> {
    protected Logger logger = LoggerFactory.getLogger(this.getClass());
	private RequestEntity requestEntity;
	
	public AbsHystrixCommand(RequestEntity requestEntity, HystrixCommandConfig config) {
        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey(config.getModelName()))
                .andCommandKey(HystrixCommandKey.Factory.asKey(config.getCommandName()))
                .andThreadPoolKey(HystrixThreadPoolKey.Factory.asKey(config.getPoolName()))
                .andThreadPoolPropertiesDefaults(HystrixThreadPoolProperties.Setter()
                        .withCoreSize(config.getCoreSize())
                        .withMaxQueueSize(config.getMaxQueueSize())
                        .withQueueSizeRejectionThreshold(config.getQueueSizeRejectionThreshold()))
                .andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
                        .withCircuitBreakerRequestVolumeThreshold(config.getCircuitBreakerReuqest())
                        .withCircuitBreakerErrorThresholdPercentage(config.getCircuitBreakerErrorPercentage())
                        .withCircuitBreakerSleepWindowInMilliseconds(config.getCircuitBreakerSleepMilliseconds())
                        .withExecutionTimeoutInMilliseconds(config.getTimeout())
                        .withFallbackIsolationSemaphoreMaxConcurrentRequests(config.getFallbackIsolationSemaphoreMaxRequest()))
        );
        this.requestEntity = requestEntity;
	}

    /**
     * 构建一个简单的hystrix的command配置对象
     * @param modelName     功能模块名称
     * @param commandName   命令名称
     * @param poolName      线程池名称
     * @return
     */
	public static HystrixCommandConfig getHystrixCommandConfig(String modelName,String commandName,String poolName){
	    return HystrixCommandConfig.getInstance(modelName, commandName, poolName);
    }

    /**
     * 失败后回调
     * @return
     */
    @Override
    protected ResponseEntity getFallback() {
        return fallBackHandler(requestEntity);
    }

    /**
     * 请求数据缓存key
     * @return
     */
    @Override
    protected String getCacheKey() {
        return super.getCacheKey();
    }

    /**
     * 真实执行请求函数
     * @return
     * @throws Exception
     */
    @Override
    protected ResponseEntity run() throws Exception {
        if(!IsDegrade.isDegrade()){
            return firstLevelHandler(requestEntity);
        }else{
            return secondLevelHandler(requestEntity);
        }
    }

    /**
     * 正常情况下执行的函数
     * @param requestEntity 请求参数
     * @return
     */
    protected abstract ResponseEntity firstLevelHandler(RequestEntity requestEntity);

    /**
     * 优雅降级后执行的函数
     * @param requestEntity 请求参数
     * @return
     */
    protected abstract ResponseEntity secondLevelHandler(RequestEntity requestEntity);

    /**
     * 函数执行失败后执行的函数
     * @param requestEntity 请求参数
     * @return
     */
    protected abstract ResponseEntity fallBackHandler(RequestEntity requestEntity);

}

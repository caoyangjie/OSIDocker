/**
 * ===================================================================================
 * <p>
 * <p>
 * <p>
 * <p>
 * ===================================================================================
 */
package com.osidocker.open.micro.hystrix;

import com.netflix.hystrix.*;
import com.osidocker.open.micro.config.HystrixCommandConfig;
import com.osidocker.open.micro.entity.IsDegrade;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @公司名称： 深圳原形信息技术有限公司
 * @类功能说明：
 * @类修改者： 曹杨杰
 * @author: Administrator
 * @创建日期： 创建于13:57 2018/3/9
 * @修改说明：
 * @修改日期： 修改于13:57 2018/3/9
 * @版本号： V1.0.0
 */
public abstract class AbsHystrixCollapserQueryCommand<ResponseEntity,RequestEntity> extends HystrixCollapser<List<ResponseEntity>, ResponseEntity, RequestEntity> {

    private RequestEntity requestEntity;
    private HystrixCommandConfig config;

    public AbsHystrixCollapserQueryCommand(RequestEntity requestEntity, HystrixCommandConfig config){
        super(Setter.withCollapserKey(HystrixCollapserKey.Factory.asKey(config.getModelName()))
                .andCollapserPropertiesDefaults(HystrixCollapserProperties.Setter()
                        .withMaxRequestsInBatch(config.getMaxRequestBatch())
                        .withTimerDelayInMilliseconds(config.getTimerDelayInMilliseconds())));
        this.config = config;
        this.requestEntity = requestEntity;
    }

    @Override
    public RequestEntity getRequestArgument() {
        return requestEntity;
    }

    @Override
    protected HystrixCommand<List<ResponseEntity>> createCommand(Collection<CollapsedRequest<ResponseEntity, RequestEntity>> requests) {
        StringBuilder paramsBuilder = new StringBuilder("");
        for(CollapsedRequest<ResponseEntity, RequestEntity> request : requests) {
            paramsBuilder.append(request.getArgument()).append(",");
        }
        String params = paramsBuilder.toString();
        params = params.substring(0, params.length() - 1);

        System.out.println("createCommand方法执行，params=" + params);

        return new BatchCommand<RequestEntity,ResponseEntity>(requests, config,
                !IsDegrade.isDegrade() ? this::firstLevelHandler : this::secondLevelHandler,
                this::fallbackHandler
        );
    }

    @Override
    protected void mapResponseToRequests(List<ResponseEntity> responseEntities, Collection<CollapsedRequest<ResponseEntity, RequestEntity>> requests) {
        int count = 0;
        for(CollapsedRequest<ResponseEntity, RequestEntity> request : requests) {
            request.setResponse(responseEntities.get(count++));
        }
    }

    static final class BatchCommand<RequestEntity,ResponseEntity> extends HystrixCommand<List<ResponseEntity>> {
        public static final String BATCH = "_Batch";
        private final Collection<CollapsedRequest<ResponseEntity, RequestEntity>> requests;
        private final Function<RequestEntity,ResponseEntity> handlerRequest;
        private final Function<RequestEntity,ResponseEntity> failBackHandlerRequest;

        public BatchCommand(Collection<CollapsedRequest<ResponseEntity, RequestEntity>> requests,
                            HystrixCommandConfig config,
                            Function<RequestEntity,ResponseEntity> handlerRequest,
                            Function<RequestEntity,ResponseEntity> failBackHandlerRequest
        ) {
            super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey(config.getModelName()+ BATCH))
                    .andCommandKey(HystrixCommandKey.Factory.asKey(config.getCommandName()+BATCH)));
            this.requests = requests;
            this.handlerRequest = handlerRequest;
            this.failBackHandlerRequest = failBackHandlerRequest;
        }

        @Override
        protected List<ResponseEntity> run() throws Exception {
            return requests.stream().map(x->handlerRequest.apply(x.getArgument())).collect(Collectors.toList());
        }

        @Override
        protected List<ResponseEntity> getFallback() {
            return requests.stream().map(x->failBackHandlerRequest.apply(x.getArgument())).collect(Collectors.toList());
        }
    }

    /**
     * 构建一个简单的hystrix的command配置对象
     * @param modelName     功能模块名称
     * @param commandName   命令名称
     * @param poolName      线程池名称
     * @return
     */
    public static HystrixCommandConfig getHystrixCommandConfig(String modelName, String commandName, String poolName){
        return HystrixCommandConfig.getInstance(modelName, commandName, poolName);
    }

    /**
     * 正常请求获取返回数据
     * @param requestEntity 请求对象实体
     * @return  返回对象实体
     */
    public abstract ResponseEntity firstLevelHandler(RequestEntity requestEntity);

    /**
     * 降级后请求获取返回数据
     * @param requestEntity 请求对象实体
     * @return  返回对象实体
     */
    public abstract ResponseEntity secondLevelHandler(RequestEntity requestEntity);

    /**
     * 请求失败后回调处理函数
     * @param requestEntity 请求对象实体
     * @return  返回对象实体
     */
    public abstract ResponseEntity fallbackHandler(RequestEntity requestEntity);

    /**
     * 用来设置缓存key
     * @return
     */
    @Override
    protected String getCacheKey() {
        return super.getCacheKey();
    }
}

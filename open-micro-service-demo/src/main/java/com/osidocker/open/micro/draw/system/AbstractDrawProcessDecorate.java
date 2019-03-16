package com.osidocker.open.micro.draw.system;

import com.osidocker.open.micro.draw.model.ActivePrize;
import com.osidocker.open.micro.draw.model.ActiveType;
import com.osidocker.open.micro.draw.service.IActivePrizeService;
import com.osidocker.open.micro.draw.service.IActiveTypeService;
import com.osidocker.open.micro.draw.service.impl.ActivePrizeServiceImpl;
import com.osidocker.open.micro.draw.service.impl.ActiveTypeServiceImpl;
import com.osidocker.open.micro.draw.system.factory.DrawProcessCacheKeyFactory;
import com.osidocker.open.micro.draw.system.transfer.DrawRequestContext;
import com.osidocker.open.micro.draw.system.transfer.DrawResponseContext;
import com.osidocker.open.micro.spring.SpringContextHolder;
import com.osidocker.open.micro.vo.CoreException;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @Description:
 * @author: caoyj
 * @date: 2019年03月11日 17:26
 * @Copyright: © 麓山云
 */
public abstract class AbstractDrawProcessDecorate<RequestContext extends DrawRequestContext,ResponseContext extends DrawResponseContext> implements IDrawProcessDecorate<ResponseContext> {

    /**
     * 一个抽奖逻辑的扩展类对象
     */
    private AbstractDrawProcessDecorate<RequestContext,ResponseContext> next;

    /**
     * 本地线程数据上下文对象
     */
    protected ThreadLocal<RequestContext> requestContext = new ThreadLocal<RequestContext>();

    /**
     * 本地线程是否已中奖标识字段
     */
    private ThreadLocal<Boolean> prize = new ThreadLocal<>();

    public AbstractDrawProcessDecorate() {
        prize.set(false);
    }


    @Override
    public ResponseContext call() {
        //执行自己的抽奖逻辑
        try {
            ResponseContext responseContext = process();
            if (isPrize()) {
                return responseContext;
            } else {
                if (Optional.ofNullable(next).isPresent()) {
                    next.initRequestContent(requestContext.get());
                    responseContext = next.call();
                    return responseContext;
                }
                return null;
            }
        }finally {
            releaseResource();
        }
    }

    public AbstractDrawProcessDecorate initRequestContent(RequestContext context){
        requestContext.set(context);
        return this;
    }

    public AbstractDrawProcessDecorate setNext(AbstractDrawProcessDecorate<RequestContext, ResponseContext> next) {
        this.next = next;
        return this;
    }

    protected ActiveType getHdActiveType() {
        ActiveType type;
        String cacheKey = DrawProcessCacheKeyFactory.getActiveTypeKey(requestContext.get().getActiveId(),requestContext.get().getActiveTypeId());
        if( requestContext.get().getProcessCacheData().containsKey(cacheKey) ){
            type = (ActiveType) requestContext.get().getProcessCacheData().get(cacheKey);
        }else{
             type = getActiveTypeService().getActiveTypeBy(requestContext.get().getActiveId(),requestContext.get().getActiveTypeId());
             requestContext.get().getProcessCacheData().putIfAbsent( cacheKey, type );
        }
        return type;
    }

    protected List<ActivePrize> getActivePrizeByType(){
        //获取活动类别 对应设置的 奖品列表
        List<ActivePrize> activePrize;
        String cacheKey = DrawProcessCacheKeyFactory.getActivePrizeListByType(requestContext.get().getActiveId(),requestContext.get().getActiveTypeId());
        if( requestContext.get().getProcessCacheData().containsKey(cacheKey) ){
            activePrize = (List<ActivePrize>) requestContext.get().getProcessCacheData().get(cacheKey);
        } else {
            Optional<List<ActivePrize>> activePrizeOpt = Optional.ofNullable(getActivePrizeService().getPrizesByTypeId(requestContext.get().getActiveId(),requestContext.get().getActiveTypeId()));
            activePrize = activePrizeOpt.orElseThrow(()->new CoreException(GunsCheckException.CheckExceptionEnum.ACTIVE_PRIZE_IS_NOT_SET));
            requestContext.get().getProcessCacheData().putIfAbsent(cacheKey,activePrize);
        }
        return activePrize;
    }

    /**
     * 自定义实现的抽奖逻辑代码
     * @return
     */
    protected abstract ResponseContext process();

    public boolean isPrize(){
        return prize.get()|| next!=null?next.isPrize():false;
    }

    protected void markPrize(){
        prize.set(true);
    }

    protected void releaseResource(){
        requestContext.set(null);
        prize.set(false);
    }

    private IActiveTypeService getActiveTypeService(){
        return SpringContextHolder.getBean(ActiveTypeServiceImpl.ACTIVE_TYPE_SERVICE_IMPL);
    }

    protected IActivePrizeService getActivePrizeService(){
        return SpringContextHolder.getBean(ActivePrizeServiceImpl.ACTIVE_PRIZE_SERVICE_IMPL);
    }
}

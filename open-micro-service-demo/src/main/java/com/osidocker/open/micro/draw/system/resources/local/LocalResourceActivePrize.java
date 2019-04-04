package com.osidocker.open.micro.draw.system.resources.local;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.osidocker.open.micro.draw.model.ActivePrize;
import com.osidocker.open.micro.draw.model.ActiveType;
import com.osidocker.open.micro.draw.service.IActivePrizeService;
import com.osidocker.open.micro.draw.service.IActiveTypeService;
import com.osidocker.open.micro.draw.service.impl.ActivePrizeServiceImpl;
import com.osidocker.open.micro.draw.service.impl.ActiveTypeServiceImpl;
import com.osidocker.open.micro.draw.system.CoreCheckException;
import com.osidocker.open.micro.draw.system.concurrent.LocalProvideCount;
import com.osidocker.open.micro.draw.system.factory.DrawConstantFactory;
import com.osidocker.open.micro.draw.system.factory.DrawProcessCacheKeyFactory;
import com.osidocker.open.micro.draw.system.resources.AbstractResourceLoadLocal;
import com.osidocker.open.micro.draw.system.transfer.DrawRequestContext;
import com.osidocker.open.micro.draw.system.transfer.DrawResponseContext;
import com.osidocker.open.micro.spring.SpringContextHolder;
import com.osidocker.open.micro.vo.CoreException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * @Description:    活动Id，活动类别，活动奖品 的发放次数
 * @author: caoyj
 * @date: 2019年03月13日 15:55
 * @Copyright: © Caoyj
 */
@Service(LocalResourceActivePrize.PROVIDE_COUNT_LOCAL_RESOURCE)
public class LocalResourceActivePrize extends AbstractResourceLoadLocal<DrawRequestContext, LocalProvideCount> {

    public static final String PROVIDE_COUNT_LOCAL_RESOURCE = "localResourceProvideCount";
    public static final int NOT_DELETE = 1;

    @Override
    protected String resourceName(DrawRequestContext ctx) {
        return DrawProcessCacheKeyFactory.getProvideCountsById(ctx.getActiveId(),ctx.getActiveTypeId());
    }

    @Override
    protected void process(DrawRequestContext ctx) {
        Optional<List<ActivePrize>> activePrizesOpt = Optional.ofNullable(getActivePartakeService().selectList(getWhere(ctx.getActiveId(),ctx.getActiveTypeId())));
        if( !activePrizesOpt.isPresent() || activePrizesOpt.get().isEmpty() ){
            throw new CoreException(CoreCheckException.CheckExceptionEnum.INIT_DB_PRIZE_IS_NOT_EXIST);
        }else{
            resourceMap.putIfAbsent(resourceName(ctx),
                new LocalProvideCount(
                    resourceName(ctx),
                    activePrizesOpt.get(),
                    getActiveTypeService().selectById(ctx.getActiveTypeId()).getChanceSum()
                )
            );
        }
    }

    @Override
    protected boolean normalFlushToDb(DrawResponseContext ctx) {
        return false;
    }

    @Override
    protected boolean drawPrizeFlushToDb(DrawResponseContext ctx) {
        LocalProvideCount count = resourceMap.get(resourceName(ctx.getRequestContext()));
        ActivePrize ap = new ActivePrize();
        ap.setId(count.getInstance(ctx.getPrizeId().toString()).getId());
        ap.setOverNum(count.incrementAndGet(ctx.getPrizeId()));
        return getActivePartakeService().updateById(ap);
    }

    @Override
    protected Stream<String> normalArgsCheck() {
        return null;
    }

    private EntityWrapper<ActivePrize> getWhere(Integer activeId,Integer activeTypeId){
        EntityWrapper<ActivePrize> where = new EntityWrapper<>();
        where.eq(DrawConstantFactory.DB_CLASS,activeId).eq(DrawConstantFactory.DB_TYPE,activeTypeId).eq(DrawConstantFactory.DB_DEL, NOT_DELETE);
        return where;
    }

    private EntityWrapper<ActiveType> getWhere(Integer activeTypeId){
        EntityWrapper<ActiveType> where = new EntityWrapper<>();
        where.eq(DrawConstantFactory.DB_ID,activeTypeId);
        return where;
    }

    private IActivePrizeService getActivePartakeService(){
        return SpringContextHolder.getBean(ActivePrizeServiceImpl.ACTIVE_PRIZE_SERVICE_IMPL);
    }

    private IActiveTypeService getActiveTypeService(){
        return SpringContextHolder.getBean(ActiveTypeServiceImpl.ACTIVE_TYPE_SERVICE_IMPL);
    }
}

package com.osidocker.open.micro.draw.system.resources.local;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.osidocker.open.micro.draw.model.ActivePartake;
import com.osidocker.open.micro.draw.service.IActivePartakeService;
import com.osidocker.open.micro.draw.service.impl.ActivePartakeServiceImpl;
import com.osidocker.open.micro.draw.system.CoreCheckException;
import com.osidocker.open.micro.draw.system.concurrent.LocalDayPartakeCount;
import com.osidocker.open.micro.draw.system.factory.DrawConstantFactory;
import com.osidocker.open.micro.draw.system.factory.DrawProcessCacheKeyFactory;
import com.osidocker.open.micro.draw.system.resources.AbstractResourceLoadLocal;
import com.osidocker.open.micro.draw.system.transfer.DrawRequestContext;
import com.osidocker.open.micro.draw.system.transfer.DrawResponseContext;
import com.osidocker.open.micro.spring.SpringContextHolder;
import com.osidocker.open.micro.utils.DateTimeKit;
import com.osidocker.open.micro.vo.CoreException;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * @Description:    统计活动Id，活动类别，每日参与次数
 * @author: caoyj
 * @date: 2019年03月12日 15:44
 * @Copyright: © Caoyj
 */
@Service(LocalResourceActivePartakeDayCount.ACTIVE_PARTAKE_RESOURCE)
public class LocalResourceActivePartakeDayCount extends AbstractResourceLoadLocal<DrawRequestContext, LocalDayPartakeCount> {

    public static final String ACTIVE_PARTAKE_RESOURCE = "localResourceDayActivePartake";

    @Override
    protected String resourceName(DrawRequestContext ctx) {
        return DrawProcessCacheKeyFactory.getActivePartakeKey();
    }

    @Override
    protected void process(DrawRequestContext ctx) {
        String dateStr = DateTimeKit.format(new Date(),DrawProcessCacheKeyFactory.YYYY_MM_DD);
        EntityWrapper<ActivePartake> where = new EntityWrapper<>();
        where.eq(DrawConstantFactory.DB_CLASS,ctx.getActiveId())
                .eq(DrawConstantFactory.DB_TYPE,ctx.getActiveTypeId())
                .eq(DrawConstantFactory.DB_DATE,dateStr);
        Optional<ActivePartake> activePartakeOpt = Optional.ofNullable(getActivePartakeService().selectOne(where));
        ActivePartake activePartake = new ActivePartake();
        if( !activePartakeOpt.isPresent() ){
            activePartake.setVisit(0);
            activePartake.setClassId(ctx.getActiveId());
            activePartake.setDate(dateStr);
            activePartake.setType(ctx.getActiveTypeId());
            if( !getActivePartakeService().insert(activePartake) ){
                throw new CoreException(CoreCheckException.CheckExceptionEnum.INSERT_ACTIVE_PARTAKE_ERROR);
            }
            resourceMap.putIfAbsent(resourceName(ctx), new LocalDayPartakeCount(activePartake));
        }else{
            activePartake = activePartakeOpt.get();
        }
        resourceMap.putIfAbsent(resourceName(ctx), new LocalDayPartakeCount(activePartake));
    }

    private IActivePartakeService getActivePartakeService(){
        return SpringContextHolder.getBean(ActivePartakeServiceImpl.ACTIVE_PARTAKE_SERVICE_IMPL);
    }

    @Override
    protected boolean normalFlushToDb(DrawResponseContext ctx) {
        return false;
    }

    @Override
    protected boolean drawPrizeFlushToDb(DrawResponseContext ctx) {
        LocalDayPartakeCount localDayPartakeCount = resourceMap.get(resourceName(ctx.getRequestContext()));
        ActivePartake ap = new ActivePartake();
        ap.setId(localDayPartakeCount.getInstance().getId());
        ap.setVisit(localDayPartakeCount.getVisit());
        return getActivePartakeService().updateById(ap);
    }

    @Override
    protected Stream<String> normalArgsCheck() {
        return null;
    }
}

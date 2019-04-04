package com.osidocker.open.micro.draw.system.resources.local;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.osidocker.open.micro.draw.model.ActivePartake;
import com.osidocker.open.micro.draw.service.IActivePartakeService;
import com.osidocker.open.micro.draw.service.impl.ActivePartakeServiceImpl;
import com.osidocker.open.micro.draw.system.concurrent.LocalAccessCount;
import com.osidocker.open.micro.draw.system.factory.DrawConstantFactory;
import com.osidocker.open.micro.draw.system.factory.DrawProcessCacheKeyFactory;
import com.osidocker.open.micro.draw.system.resources.AbstractResourceLoadLocal;
import com.osidocker.open.micro.draw.system.transfer.DrawRequestContext;
import com.osidocker.open.micro.draw.system.transfer.DrawResponseContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * @Description:    统计 活动Id，活动类别下的 参与总次数
 * @author: caoyj
 * @date: 2019年03月12日 8:44
 * @Copyright: © Caoyj
 */
@Service(LocalResourceSumAccessCount.ACCESS_COUNT_RESOURCE)
public class LocalResourceSumAccessCount extends AbstractResourceLoadLocal<DrawRequestContext, LocalAccessCount> {

    public static final String ACCESS_COUNT_RESOURCE = "accessCountLocalResource";
    @Autowired
    @Qualifier(ActivePartakeServiceImpl.ACTIVE_PARTAKE_SERVICE_IMPL)
    protected IActivePartakeService service;

    @Override
    protected void process(DrawRequestContext ctx){
        //构建数据库查询请求参数
        EntityWrapper<ActivePartake> where = new EntityWrapper<>();
        where.eq(DrawConstantFactory.DB_CLASS,ctx.getActiveId())
                .eq(DrawConstantFactory.DB_TYPE,ctx.getActiveTypeId());
        //获取数据中访问次数记录sum值
        Optional<List<ActivePartake>> activePartakesOpt = Optional.ofNullable(service.selectList(where));
        if( activePartakesOpt.isPresent() ){
            int accessCountLong = activePartakesOpt.get().stream().flatMapToInt(ap->IntStream.of(ap.getVisit())).sum();
            AtomicInteger atomicLong = new AtomicInteger(accessCountLong);
            resourceMap.putIfAbsent(resourceName(ctx),new LocalAccessCount(atomicLong));
        }else{
            resourceMap.putIfAbsent(resourceName(ctx),new LocalAccessCount(new AtomicInteger(1)));
        }
    }

    @Override
    protected String resourceName(DrawRequestContext ctx) {
        return DrawProcessCacheKeyFactory.getAccessCountKey(ctx.getActiveId(),ctx.getActiveTypeId());
    }

    @Override
    protected boolean normalFlushToDb(DrawResponseContext ctx) {
        return true;
    }

    @Override
    protected boolean drawPrizeFlushToDb(DrawResponseContext ctx) {
        return true;
    }

    @Override
    protected Stream<String> normalArgsCheck() {
        return Stream.empty();
    }
}

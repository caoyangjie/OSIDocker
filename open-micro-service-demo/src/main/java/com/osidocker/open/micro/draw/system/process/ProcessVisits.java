package com.osidocker.open.micro.draw.system.process;

import com.osidocker.open.micro.draw.model.ActivePrize;
import com.osidocker.open.micro.draw.model.ActiveType;
import com.osidocker.open.micro.draw.system.AbstractDrawProcessDecorate;
import com.osidocker.open.micro.draw.system.concurrent.LocalProvideCount;
import com.osidocker.open.micro.draw.system.factory.DrawConstantFactory;
import com.osidocker.open.micro.draw.system.resources.AbstractResourceLoadLocal;
import com.osidocker.open.micro.draw.system.resources.local.LocalResourceActivePrize;
import com.osidocker.open.micro.draw.system.transfer.DrawRequestContext;
import com.osidocker.open.micro.draw.system.transfer.DrawResponseContext;
import com.osidocker.open.micro.spring.SpringContextHolder;

import java.util.*;

/**
 * @Description:
 * @author: caoyj
 * @date: 2019年03月11日 17:53
 * @Copyright: © Caoyj
 */
public class ProcessVisits extends AbstractDrawProcessDecorate<DrawRequestContext, DrawResponseContext> {


    public static final String PROCESS_VISTIS = "processVistis";

    @Override
    protected DrawResponseContext process() {
        //访问次数+1
        final Integer accessCount = Optional.ofNullable(sumAccessCount()).get().getInstance().get();
        //获取活动类别对象
        ActiveType type = getActiveType();
        //1是指定次数出奖,其他不是指定次数抽奖
        if( type.getIsVisit()!=1 ){
            return null;
        }
        //执行抽奖核心逻辑代码 TODO 这里代码有问题,如果不同的奖品设置相同的访问次数中奖则会出现问题
        Optional<ActivePrize> activePrizeOptional =
                //获取 活动类别下的活动奖品列表
                drawPrizeList().getResource(requestContext.get()).getActivePrizeList().parallelStream()
                //抽奖逻辑
                .filter( pri-> Arrays.asList( pri.getVisits().split(DrawConstantFactory.REGEX) ).contains(accessCount.toString()))
                .findFirst();
        if( activePrizeOptional.isPresent() ){
            //中奖啦!恭喜恭喜!
            return getSuccessPrize(type,activePrizeOptional.get(), PROCESS_VISTIS);
        }
        //
        return null;
    }


    protected AbstractResourceLoadLocal<DrawRequestContext, LocalProvideCount> drawPrizeList(){
        return SpringContextHolder.getBean(LocalResourceActivePrize.PROVIDE_COUNT_LOCAL_RESOURCE);
    }

}

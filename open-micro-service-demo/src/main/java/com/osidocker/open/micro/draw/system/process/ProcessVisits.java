package com.osidocker.open.micro.draw.system.process;

import com.osidocker.open.micro.draw.model.ActivePrize;
import com.osidocker.open.micro.draw.model.ActiveType;
import com.osidocker.open.micro.draw.system.AbstractDrawProcessDecorate;
import com.osidocker.open.micro.draw.system.concurrent.LocalAccessCount;
import com.osidocker.open.micro.draw.system.concurrent.LocalProvideCount;
import com.osidocker.open.micro.draw.system.factory.DrawConstantFactory;
import com.osidocker.open.micro.draw.system.resources.AbstractResourceLoadLocal;
import com.osidocker.open.micro.draw.system.resources.local.LocalResourceActivePrize;
import com.osidocker.open.micro.draw.system.resources.local.LocalResourceSumAccessCount;
import com.osidocker.open.micro.draw.system.transfer.DrawRequestContext;
import com.osidocker.open.micro.draw.system.transfer.DrawResponseContext;
import com.osidocker.open.micro.spring.SpringContextHolder;

import java.util.*;

/**
 * @Description:
 * @author: caoyj
 * @date: 2019年03月11日 17:53
 * @Copyright: © 麓山云
 */
public class ProcessVisits extends AbstractDrawProcessDecorate<DrawRequestContext, DrawResponseContext> {

    @Override
    protected DrawResponseContext process() {
        //访问次数+1
        final Integer accessCount = Optional.ofNullable(accessCountRef().getResource(requestContext.get())).get().getInstance().incrementAndGet();
        //获取活动类别对象
        ActiveType type = getHdActiveType();
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
            markPrize();
            return getSuccessPrize(type,activePrizeOptional.get());
        }
        //
        return null;
    }

    /**
     * 获取访问
     * @return
     */
    protected AbstractResourceLoadLocal<DrawRequestContext, LocalAccessCount> accessCountRef(){
        return SpringContextHolder.getBean(LocalResourceSumAccessCount.ACCESS_COUNT_RESOURCE);
    }

    protected AbstractResourceLoadLocal<DrawRequestContext, LocalProvideCount> drawPrizeList(){
        return SpringContextHolder.getBean(LocalResourceActivePrize.PROVIDE_COUNT_LOCAL_RESOURCE);
    }

    private DrawResponseContext getSuccessPrize(ActiveType type,ActivePrize prize){
        DrawResponseContext responseContext = new DrawResponseContext();
        responseContext.setRequestContext(requestContext.get());
        Map<String,Object> transData = new HashMap<>();
        transData.put("prize_id",prize.getId());
        transData.put("prize_name",prize.getName());
        transData.put("prize_pic",prize.getPrizePic());
        transData.put("class",type.getActiveId());
        transData.put("mold",prize.getMold());
        //TODO 获取中奖Id
        transData.put("win_id",-1);
        transData.put("sort",prize.getSort());
        transData.put("amount",prize.getAmount());
        //TODO 获取大转盘角度
        transData.put("angle",-1);
        transData.put("remark",prize.getRemark());
        responseContext.getTransData().putAll(transData);
        return responseContext;
    }
}

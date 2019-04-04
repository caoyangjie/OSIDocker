package com.osidocker.open.micro.draw.system.process;

import com.osidocker.open.micro.draw.model.ActivePrize;
import com.osidocker.open.micro.draw.model.ActiveType;
import com.osidocker.open.micro.draw.system.AbstractDrawProcessDecorate;
import com.osidocker.open.micro.draw.system.concurrent.LocalAccessCount;
import com.osidocker.open.micro.draw.system.concurrent.LocalProvideCount;
import com.osidocker.open.micro.draw.system.resources.AbstractResourceLoadLocal;
import com.osidocker.open.micro.draw.system.resources.local.LocalResourceActivePrize;
import com.osidocker.open.micro.draw.system.resources.local.LocalResourceSumAccessCount;
import com.osidocker.open.micro.draw.system.transfer.DrawRequestContext;
import com.osidocker.open.micro.draw.system.transfer.DrawResponseContext;
import com.osidocker.open.micro.spring.SpringContextHolder;

import java.util.Random;

/**
 * @Description:
 * @author: caoyj
 * @date: 2019年03月28日 15:24
 * @Copyright: © Caoyj
 */
public class ProcessWeight extends AbstractDrawProcessDecorate<DrawRequestContext, DrawResponseContext> {

    public static final String PROCESS_WEIGHT = "processWeight";

    @Override
    protected DrawResponseContext process() {
        //获取活动类别对象
        ActiveType type = getActiveType();
        //生成一个指定奖池数量访问内的一个随机数
        int random = new Random(getAccessCount().getInstance().get()).nextInt(type.getChanceSum());
        LocalProvideCount resource = drawPrizeList().getResource(requestContext.get());
        //获取奖池map中,当前随机数是否存在
        Integer prizeId = resource.getJackPotMap().getOrDefault(random+"",-1);
        if( prizeId == -1 ){
            return null;
        }
        ActivePrize prize = resource.getInstance(prizeId+"");
        //返回的值 不为空 代表抽中了奖品
        if( prize != null ){
            return getSuccessPrize(type,prize, PROCESS_WEIGHT);
        }
        return null;
    }


    protected AbstractResourceLoadLocal<DrawRequestContext, LocalProvideCount> drawPrizeList(){
        return SpringContextHolder.getBean(LocalResourceActivePrize.PROVIDE_COUNT_LOCAL_RESOURCE);
    }

    protected LocalAccessCount getAccessCount(){
        return ((LocalResourceSumAccessCount)SpringContextHolder.getBean(LocalResourceSumAccessCount.ACCESS_COUNT_RESOURCE)).getResource(requestContext.get());
    }

}

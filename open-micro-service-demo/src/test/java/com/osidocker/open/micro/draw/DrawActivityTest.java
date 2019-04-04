package com.osidocker.open.micro.draw;

import com.osidocker.open.micro.base.BaseJunit;
import com.osidocker.open.micro.draw.system.AbstractDrawStrategy;
import com.osidocker.open.micro.draw.system.factory.DrawPrizeProcessFactory;
import com.osidocker.open.micro.draw.system.impl.CacheSuccessDataQueue;
import com.osidocker.open.micro.draw.system.impl.DrawPrizePartakeService;
import com.osidocker.open.micro.draw.system.impl.SuccessDataExecutorThread;
import com.osidocker.open.micro.draw.system.resources.local.LocalResourceActivePartakeDayCount;
import com.osidocker.open.micro.draw.system.resources.local.LocalResourceActivePrize;
import com.osidocker.open.micro.draw.system.resources.local.LocalResourceActivePrizeStatistics;
import com.osidocker.open.micro.draw.system.transfer.DrawRequestContext;
import com.osidocker.open.micro.draw.system.transfer.DrawResponseContext;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.*;
import java.util.stream.Stream;

/**
 * @Description:
 * @author: caoyj
 * @date: 2019年03月09日 17:37
 * @Copyright: © Caoyj
 */
public class DrawActivityTest extends BaseJunit {

    @Autowired
    @Qualifier(DrawPrizePartakeService.DRAW_PRIZE_PARTAKE_SERVICE)
    AbstractDrawStrategy<DrawRequestContext, DrawResponseContext> drawService;

    @Autowired
    LocalResourceActivePartakeDayCount apdc;

    @Autowired
    LocalResourceActivePrize lrap;

    @Autowired
    LocalResourceActivePrizeStatistics lraps;

    public DrawRequestContext initRequestArgs(Integer i){
        DrawRequestContext requestContext = new DrawRequestContext();
        requestContext.setDrawEnums(1);
        Map args = new HashMap();
        args.put("receive_type",5);
        args.put("class",16);
        args.put("uid",(1+(i%10000)));
        args.put("nickname","公安机关"+(1+i%10000));
        args.put("sign","88888888");
        args.put("token","caoyj");
        requestContext.setIp("192.168.188.100");
        requestContext.setTransData(args);
        return requestContext;
    }

    @Test
    public void drawActivityTest() throws InterruptedException {
        List<Long[]> times = new ArrayList<>();
//        times.add(executorCounts(100));
//        System.out.println("------------------------------------------");
//        times.add(executorCounts(1000));
//        System.out.println("------------------------------------------");
        times.add(executorCounts(20000));
        times.forEach(t->{
            System.out.println("抽奖时间："+t[0]+"中奖次数："+t[1]+"刷入次数："+t[2]);
        });
        Thread.sleep(5000);
    }

    private Long[] executorCounts(int count) throws InterruptedException {
        DrawPrizeProcessFactory.successAdd.reset();
        DrawPrizeProcessFactory.flushSucc.reset();
        SuccessDataExecutorThread.work(Runtime.getRuntime().availableProcessors());
        long start = System.currentTimeMillis();
        Stream.iterate(1, i->++i).limit(count).parallel().forEach(i->Stream.of(drawService.execute(initRequestArgs(i))));
        while (!CacheSuccessDataQueue.getInstance().isEmpty()){
            Thread.sleep(1000L);
        }
        System.out.println("抽奖时间："+(System.currentTimeMillis()-start));
        System.out.println("中奖累计次数："+ DrawPrizeProcessFactory.successAdd.sum());
        System.out.println("刷入数据库次数："+DrawPrizeProcessFactory.flushSucc.sum());
        return new Long[]{System.currentTimeMillis()-start,DrawPrizeProcessFactory.successAdd.sum(),DrawPrizeProcessFactory.flushSucc.sum()};
    }
}

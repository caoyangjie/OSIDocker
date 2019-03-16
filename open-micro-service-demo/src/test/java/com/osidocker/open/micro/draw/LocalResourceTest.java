package com.osidocker.open.micro.draw;

import com.osidocker.open.micro.base.BaseJunit;
import com.osidocker.open.micro.draw.system.concurrent.LocalAccessCount;
import com.osidocker.open.micro.draw.system.concurrent.LocalActivePartakeStatistics;
import com.osidocker.open.micro.draw.system.concurrent.LocalDayPartakeCount;
import com.osidocker.open.micro.draw.system.concurrent.LocalProvideCount;
import com.osidocker.open.micro.draw.system.factory.DrawPrizeProcessFactory;
import com.osidocker.open.micro.draw.system.resources.AbstractResourceLoad;
import com.osidocker.open.micro.draw.system.resources.local.LocalResourceActivePartakeDayCount;
import com.osidocker.open.micro.draw.system.resources.local.LocalResourceActivePrize;
import com.osidocker.open.micro.draw.system.resources.local.LocalResourceActivePrizeStatistics;
import com.osidocker.open.micro.draw.system.resources.local.LocalResourceSumAccessCount;
import com.osidocker.open.micro.draw.system.transfer.DrawRequestContext;
import com.osidocker.open.micro.utils.DateTimeKit;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.stream.Stream;

/**
 * @Description:
 * @author: caoyj
 * @date: 2019年03月13日 9:36
 * @Copyright: © 麓山云
 */
public class LocalResourceTest extends BaseJunit {

    @Autowired
    @Qualifier(LocalResourceSumAccessCount.ACCESS_COUNT_RESOURCE)
    protected AbstractResourceLoad<DrawRequestContext, LocalAccessCount> accessCountLoad;

    @Autowired
    @Qualifier(LocalResourceActivePartakeDayCount.ACTIVE_PARTAKE_RESOURCE)
    protected AbstractResourceLoad<DrawRequestContext, LocalDayPartakeCount> activePartakeLoad;

    @Autowired
    @Qualifier(LocalResourceActivePrize.PROVIDE_COUNT_LOCAL_RESOURCE)
    protected AbstractResourceLoad<DrawRequestContext, LocalProvideCount> provideCountLoad;

    @Autowired
    @Qualifier(LocalResourceActivePrizeStatistics.ACTIVE_PARTAKE_STATISTICS_RESOURCE)
    protected AbstractResourceLoad<DrawRequestContext, LocalActivePartakeStatistics> activePrizeLoad;

    private DrawRequestContext requestContext = new DrawRequestContext();

    @Before
    public void initArgs(){
        HashMap transData = new HashMap();
        transData.put("start", DateTimeKit.offsiteWeek(new Date(),-2).toDate());
        transData.put("end", DateTimeKit.offsiteWeek(new Date(),1).toDate());
        transData.put("receive_type",4);
        transData.put("class",15);
        transData.put("uid",11);
        transData.put("nickname","这是昵称");
        transData.put("sign","88888888");
        requestContext.setDrawEnums(1);
        requestContext.setToken("password");
        requestContext.setUseTokenFlag(true);
        requestContext.setTransData(transData);
    }

    private void testConcurrentResource(AbstractResourceLoad resourceLoad, Callable<Integer> task) throws ExecutionException, InterruptedException {
        List<Future<Integer>> futures = new ArrayList<>(1000);
        Stream.iterate(1,i->i+1).limit(100).forEach(i->{
            futures.add(DrawPrizeProcessFactory.drawPrizePool.submit( task ) );
        });
        int max=0,min=1000,val;
        for (int i = 0; i < futures.size(); i ++){
            System.out.println("访问次数:"+(val=futures.get(i).get()));
            max = max>val?max:val;
            min = min>val?val:min;
        }
        System.out.println("去重后返回的值个数为:"+futures.stream().distinct().count()+"最大值为:"+max+"最小值:"+min);
    }

    @Test
    public void testActivePrizeStatistic() {

        try {
            testConcurrentResource(activePrizeLoad,()-> activePrizeLoad.getResource(requestContext).incrementAndGet(new Random().nextBoolean()?11:14));
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("每日次数:"+activePrizeLoad.getResource(requestContext).countInDay(11));
        System.out.println("每周次数:"+activePrizeLoad.getResource(requestContext).countInWeek(11));
        System.out.println("每月次数:"+activePrizeLoad.getResource(requestContext).countInMouth(11));
        System.out.println("每日次数:"+activePrizeLoad.getResource(requestContext).countInDay(14));
        System.out.println("每周次数:"+activePrizeLoad.getResource(requestContext).countInWeek(14));
        System.out.println("每月次数:"+activePrizeLoad.getResource(requestContext).countInMouth(14));
        System.out.println("每日次数:"+activePrizeLoad.getResource(requestContext).countInDay(8));
        System.out.println("每周次数:"+activePrizeLoad.getResource(requestContext).countInWeek(8));
        System.out.println("每月次数:"+activePrizeLoad.getResource(requestContext).countInMouth(8));
    }

    @Test
    public void testProvideCountLocal() throws ExecutionException, InterruptedException {
        List<Future<Integer>> futures = new ArrayList<>(1000);
        Stream.iterate(1,i->i+1).limit(100).forEach(i->{
            futures.add(DrawPrizeProcessFactory.drawPrizePool.submit(()-> provideCountLoad.getResource(requestContext).incrementAndGet(i%2==1?7:8)));
        });
        for (int i = 0; i < futures.size(); i ++){
            System.out.println("访问次数:"+futures.get(i).get());
        }
        System.out.println("最后一次访问次数:"+provideCountLoad.getResource(requestContext).incrementAndGet(7));
        System.out.println("最后一次访问次数:"+provideCountLoad.getResource(requestContext).incrementAndGet(8));
    }

    @Test
    public void testTwoResourceLocal() throws ExecutionException, InterruptedException {
        List<Future<Integer>> result1 = new ArrayList<>(10000);
        List<Future<Integer>> result2 = new ArrayList<>(1000);
        Stream.iterate(1,i->i+1).limit(100).forEach(i->{
            result1.add(DrawPrizeProcessFactory.drawPrizePool.submit(()-> accessCountLoad.getResource(requestContext).getInstance().incrementAndGet()));
            result2.add(DrawPrizeProcessFactory.drawPrizePool.submit(()-> activePartakeLoad.getResource(requestContext).incrementAndGet()));
        });
        for (int i = 0; i < result2.size(); i ++){
            System.out.println("这是结果:"+result2.get(i).get());
        }
        for(int i = 0; i < result1.size(); i ++){
            System.out.println("输出结果值"+result1.get(i).get());
        }
        System.out.println(accessCountLoad.getResource(requestContext).getInstance().incrementAndGet());
        System.out.println(activePartakeLoad.getResource(requestContext).getVisit());
    }

    @Test
    public void testActivePartakeLocal() throws ExecutionException, InterruptedException {
        List<Future<Integer>> result = new ArrayList<>(1000);
        Stream.iterate(1,i->i+1).limit(100).forEach(i->{
            result.add(DrawPrizeProcessFactory.drawPrizePool.submit(()-> activePartakeLoad.getResource(requestContext).incrementAndGet()));
        });
        int max=0,min=1000,val;
        for (int i = 0; i < result.size(); i ++){
            System.out.println("这是结果:"+(val=result.get(i).get()));
            max = max>val?max:val;
            min = min>val?val:min;
        }
        System.out.println("最后一次访问:"+activePartakeLoad.getResource(requestContext).getVisit());
        System.out.println("去重后返回的值个数为:"+result.stream().distinct().count()+"最大值为:"+max+"最小值:"+min);
    }

    @Test
    public void testAccessCountLocal(){
        List<Integer> longList = new ArrayList<>(10000);
        //初始化 load
//        initLoad(accessCountLoad);
        List<Future<Integer>> result = new ArrayList<>(10000);
        Stream.iterate(1,i->i+1).limit(100).forEach(i->{
            result.add(DrawPrizeProcessFactory.drawPrizePool.submit(()-> accessCountLoad.getResource(requestContext).getInstance().incrementAndGet()));
        });

        for(int i = 0; i < result.size(); i ++){
            try {
                Integer res = result.get(i).get();
                longList.add(res);
                System.out.println("输出结果值"+result.get(i).get());
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
        System.out.println("去重后，获取的list的count数量"+longList.stream().distinct().count());
    }
}

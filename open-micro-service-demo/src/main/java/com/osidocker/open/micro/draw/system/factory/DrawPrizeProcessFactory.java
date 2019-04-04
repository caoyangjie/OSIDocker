package com.osidocker.open.micro.draw.system.factory;

import com.osidocker.open.micro.draw.system.AbstractDrawProcessDecorate;
import com.osidocker.open.micro.draw.system.process.ProcessVisits;
import com.osidocker.open.micro.draw.system.process.ProcessWeight;
import com.osidocker.open.micro.draw.system.transfer.DrawRequestContext;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.LongAdder;

/**
 * @Description:
 * @author: caoyj
 * @date: 2019年03月12日 18:02
 * @Copyright: © Caoyj
 */
public class DrawPrizeProcessFactory {

    /**
     * 抽奖线程池
     */
    public static ExecutorService drawPrizePool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    /**
     * 中奖后数据持久化的线程池
     */
    public static ExecutorService flushDbPool = Executors.newFixedThreadPool(50);

    /**
     * 抽中奖总次数
     */
    public static LongAdder successAdd = new LongAdder();

    /**
     * 刷入数据库的成功次数
     */
    public static LongAdder flushSucc = new LongAdder();


    /**
     * 初始化 visits 抽奖器
     */
    public static AbstractDrawProcessDecorate getDrawProcess(DrawRequestContext ctx){
        ProcessVisits process = new ProcessVisits();
        process.setNext(new ProcessWeight());
        return process.initRequestContent(ctx);
    }

}

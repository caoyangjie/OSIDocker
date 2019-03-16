package com.osidocker.open.micro.draw.system.factory;

import com.osidocker.open.micro.draw.system.AbstractDrawProcessDecorate;
import com.osidocker.open.micro.draw.system.process.ProcessAngle;
import com.osidocker.open.micro.draw.system.process.ProcessVisits;
import com.osidocker.open.micro.draw.system.transfer.DrawRequestContext;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Description:
 * @author: caoyj
 * @date: 2019年03月12日 18:02
 * @Copyright: © 麓山云
 */
public class DrawPrizeProcessFactory {

    /**
     * 抽奖线程池
     */
    public static ExecutorService drawPrizePool = Executors.newFixedThreadPool(500);

    /**
     * 中奖后数据持久化的线程池
     */
    public static ExecutorService flushDbPool = Executors.newFixedThreadPool(20);
    /**
     * 初始化 visits 抽奖器
     */
    private static ProcessVisits process = new ProcessVisits();
    static{
        /**
         * 初始化 draw 抽奖 逻辑处理器
         */
        process.setNext(new ProcessAngle());
    }

    public static AbstractDrawProcessDecorate getDrawProcess(DrawRequestContext ctx){
        return process.initRequestContent(ctx);
    }

}

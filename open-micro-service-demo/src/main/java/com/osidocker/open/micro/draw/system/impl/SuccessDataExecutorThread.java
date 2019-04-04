package com.osidocker.open.micro.draw.system.impl;

import com.osidocker.open.micro.draw.system.AbstractDrawStrategy;
import com.osidocker.open.micro.draw.system.IMessageQueue;
import com.osidocker.open.micro.draw.system.factory.DrawPrizeProcessFactory;
import com.osidocker.open.micro.draw.system.transfer.DrawRequestContext;
import com.osidocker.open.micro.draw.system.transfer.DrawResponseContext;
import com.osidocker.open.micro.spring.SpringContextHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;


/**
 * @Description:
 * @author: caoyj
 * @date: 2019年03月29日 18:21
 * @Copyright: © Caoyj
 */
public class SuccessDataExecutorThread implements Runnable {
    public static volatile boolean stop = false;

    public static void work(int threadNum) {
        List<Future<?>> result = new ArrayList();
        for (int i = 0; i < threadNum; i ++){
            result.add(DrawPrizeProcessFactory.flushDbPool.submit(new SuccessDataExecutorThread()));
        }
    }

    @Override
    public void run() {
        //执行中奖后的处理逻辑
        while (!stop){
            DrawResponseContext response = null;
            try{
                response = (DrawResponseContext) CacheSuccessDataQueue.getInstance().take();
                if( response!=null && response.isPrizeFlag() ){
                    //将中奖后的信息持久化到数据库中
                    if( getDrawPrizeService(response).flushDataToDb(response) ){
                        DrawPrizeProcessFactory.flushSucc.increment();
                        response.setRequestContext(null);
                        response.getTransData().clear();
                        response = null;
                    }
                }
            }catch(Exception e){
                if( response.getRetry() < 3 ){
                    response.plusTry();
                    CacheSuccessDataQueue.getInstance().put(response);
                }else{
                    pushMessage(response);
                }
                e.getStackTrace();
            }finally{
            }
        }
    }

    private void pushMessage(DrawResponseContext response) {
        ((IMessageQueue<DrawResponseContext>) SpringContextHolder.getBean(DefaultMessageQueue.DEFAULT_MESSAGE_QUEUE)).push(response);
    }

    private AbstractDrawStrategy<DrawRequestContext,DrawResponseContext> getDrawPrizeService(DrawResponseContext response){
        return SpringContextHolder.getBean(response.getBeanName());
    }


}

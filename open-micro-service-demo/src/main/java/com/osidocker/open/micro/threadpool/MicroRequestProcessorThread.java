package com.osidocker.open.micro.threadpool;

import com.osidocker.open.micro.request.IRequest;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;

/**
 * 执行队列内的请求处理
 *
 * @author Administrator
 * @creato 2017-12-02 15:10
 */
public class MicroRequestProcessorThread implements Callable{
    private ArrayBlockingQueue<IRequest> iRequestArrayBlockingQueue;

    public MicroRequestProcessorThread(ArrayBlockingQueue<IRequest> iRequests) {
        iRequestArrayBlockingQueue = iRequests;
    }

    @Override
    public Boolean call() throws Exception {
        try{
            while (true){
                //ArrayBlockingQueue
                //Blocking就是说明,如果队列满了或者队列空了都会在take的时候阻塞住
                IRequest request = iRequestArrayBlockingQueue.take();
                //执行request的操作
                request.handler();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }
}

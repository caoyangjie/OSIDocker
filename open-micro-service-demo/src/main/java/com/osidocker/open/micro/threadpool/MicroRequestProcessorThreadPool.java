package com.osidocker.open.micro.threadpool;

import com.osidocker.open.micro.request.IRequest;
import com.osidocker.open.micro.request.RequestQueues;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Stream;

/**
 * 用来进行数据处理的线程池对象类
 *
 * @author Administrator
 * @creato 2017-12-02 14:32
 */
public class MicroRequestProcessorThreadPool {

    /**
     * 线程池
     */
    private ExecutorService threadPool = Executors.newFixedThreadPool(20);

    private MicroRequestProcessorThreadPool() {
        RequestQueues requestQueues = RequestQueues.getInstance();
        Stream.iterate(1,x->x+1).limit(10).forEach(x-> {
            ArrayBlockingQueue<IRequest> queue = new ArrayBlockingQueue<IRequest>(100);
            requestQueues.addQueue(queue);
            threadPool.submit(new MicroRequestProcessorThread(queue));
        });
    }

    private static class Singleton{
        private static MicroRequestProcessorThreadPool instance;

        static{
            instance = new MicroRequestProcessorThreadPool();
        }

        public static MicroRequestProcessorThreadPool getInstance(){
            return instance;
        }
    }

    public static MicroRequestProcessorThreadPool getInstance(){
        return Singleton.getInstance();
    }
}

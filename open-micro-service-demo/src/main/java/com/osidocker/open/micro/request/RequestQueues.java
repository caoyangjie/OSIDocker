package com.osidocker.open.micro.request;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 请求内存队列
 *
 * @author Administrator
 * @creato 2017-12-02 15:29
 */
public class RequestQueues {
    /**
     * 内存队列
     */
    private List<ArrayBlockingQueue<IRequest>> queues = new ArrayList<ArrayBlockingQueue<IRequest>>();

    private ConcurrentHashMap<String,Boolean> currentExecMap = new ConcurrentHashMap<>();

    private static class Singleton{
        private static RequestQueues instance;

        static {
            instance = new RequestQueues();
        }

        public static RequestQueues getInstance(){
            return instance;
        }
    }

    public static RequestQueues getInstance(){
        return Singleton.getInstance();
    }

    public void addQueue(ArrayBlockingQueue<IRequest> queue){
        queues.add(queue);
    }

    public int queueSize(){
        return queues.size();
    }

    public ArrayBlockingQueue<IRequest> getQueue(int index){
        return queues.get(index);
    }

    public ConcurrentHashMap<String, Boolean> getCurrentExecMap(){
        return this.currentExecMap;
    }
}

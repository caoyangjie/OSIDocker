package com.osidocker.open.micro.service.impl;

import com.osidocker.open.micro.request.IRequest;
import com.osidocker.open.micro.request.RequestQueues;
import com.osidocker.open.micro.service.IRequestAsyncProcessService;
import org.springframework.stereotype.Service;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * 请求异步处理的实现类
 *
 * @author Administrator
 * @creato 2017-12-02 16:40
 */
@Service("requestAsynProcessServiceImpl")
public class RequestAsyncProcessServiceImpl implements IRequestAsyncProcessService{
    @Override
    public void process(IRequest request) {
        try {
            //做请求的路由,根据每个请求的商品Id,路由到对应的内存队列中
            ArrayBlockingQueue<IRequest> queue = getRoutingQueue(request.getHashKey());
            //将请求放入对应的内存队列中,完成路由操作
            queue.put(request);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private ArrayBlockingQueue<IRequest> getRoutingQueue(String hashKey){
        RequestQueues requestQueues = RequestQueues.getInstance();
        //获取productId的hash值
        int h = hashKey.hashCode();
        int hash = (hashKey==null)?0: h ^ (h >>> 16);
        int index = (requestQueues.queueSize()-1) & hash;
        return requestQueues.getQueue(index);
    }
}

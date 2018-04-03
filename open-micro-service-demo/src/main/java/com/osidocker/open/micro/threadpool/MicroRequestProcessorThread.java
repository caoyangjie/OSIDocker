package com.osidocker.open.micro.threadpool;

import com.osidocker.open.micro.request.IRequest;
import com.osidocker.open.micro.request.RequestQueues;
import com.osidocker.open.micro.request.productInventory.ProductInventoryCacheReloadRequest;
import com.osidocker.open.micro.request.productInventory.ProductInventoryUpdateRequest;

import java.util.Map;
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
                //如果是强制刷新缓存,则不执行去重操作
                if( !request.isForceRefresh() ){
                    //获取内存队列对象
                    RequestQueues requestQueues = RequestQueues.getInstance();
                    Map<String,Boolean> execMap = requestQueues.getCurrentExecMap();
                    //判断请求是否为更新请求,如果是则直接设置为true
                    if( request instanceof ProductInventoryUpdateRequest){
                        execMap.put(request.getHashKey(),true);
                    }else if( request instanceof ProductInventoryCacheReloadRequest){
                        Boolean flag = execMap.get(request.getHashKey());
                        //已经存在一个读请求,当前读请求可以取消
                        if( flag!=null && !flag ) {
                            continue;
                        }
                        //存在一个更新请求,或者完全不存在这个请求,则需要在后面追加一个读请求更新缓存数据
                        if( flag==null || flag ){
                            execMap.put(request.getHashKey(),false);
                        }
                    }
                }
                //执行request的操作
                request.handler();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }
}

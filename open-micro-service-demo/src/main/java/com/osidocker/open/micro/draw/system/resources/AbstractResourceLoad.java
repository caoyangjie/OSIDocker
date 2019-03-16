package com.osidocker.open.micro.draw.system.resources;

import com.osidocker.open.micro.draw.system.AbstractFlushData;
import com.osidocker.open.micro.draw.system.IResourceLoad;
import com.osidocker.open.micro.draw.system.transfer.DrawRequestContext;
import com.osidocker.open.micro.lock.ILockResource;
import com.osidocker.open.micro.vo.CoreException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.concurrent.locks.Lock;

/**
 * @Description:
 * @author: caoyj
 * @date: 2019年03月13日 8:48
 * @Copyright: © 麓山云
 */
public abstract class AbstractResourceLoad <RequestContent extends DrawRequestContext,Source> extends AbstractFlushData implements IResourceLoad<RequestContent,Source> {

    protected Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());

    //TODO 这里需要从配置中读取

    /**
     * 根据请求参数加载请求资源数据
     * @param ctx   请求数据上下文
     * @throws CoreException    外抛异常
     */
    private void load(RequestContent ctx) throws CoreException {
        //判断当前资源是否已经存在,如果不存在则回溯数据源
        String resourceName = resourceName(ctx);
        /**
         * 获取所资源
         */
        Lock lock = getLockResource().tryGetLock(resourceName);
        if( lock.tryLock() ){
            //TODO info("获取锁资源:{}",resourceName);
            try{
                System.out.println("获取锁资源的线程实例:"+resourceName+Thread.currentThread().getName());
                process(ctx);
            }finally {
                //释放锁资源
                lock.unlock();
            }
        }else{
            System.out.println("未能获取锁资源:"+resourceName+",已经有其他线程在加载此锁资源对应的数据!");
            // 等待获取锁的线程将资源数据加载成功后,释放当前线程
            long waitTime = 0L;
            while ( !containsResource(ctx) ){
                if( waitTime > getMaxWaitLoadTime() ){
                    break;
                }
                try {
                    waitTime += 10L;
                    Thread.sleep(10L);
                } catch (InterruptedException e) {
                }
            }
        }
    }

    /**
     * 未能获取到锁的 其他线程等待 资源加载的最大时间
     */
    protected long getMaxWaitLoadTime(){
        return 1000L;
    }

    @Override
    public final Source getResource(RequestContent ctx){
        if( !containsResource(ctx) ){
            load(ctx);
        }
        return getSource(ctx);
    }

    /**
     * 是否强制更新缓存数据
     * @return
     */
    @Override
    public boolean isForceRefresh() {
        return false;
    }

    /**
     * 是否已经拥有资源数据
     * @param ctx
     * @return
     */
    protected abstract boolean containsResource(RequestContent ctx);

    /**
     * 根据请求参数获取资源数据
     * @param ctx
     * @return
     */
    protected abstract Source getSource(RequestContent ctx);

    /**
     * 根据请求上下文获取资源名称
     * @param ctx
     * @return
     */
    protected abstract String resourceName(RequestContent ctx);

    /**
     * 获取锁实现功能实例对象
     * @return
     */
    protected abstract ILockResource getLockResource();

    /**
     * 执行自定义数据加载处理
     * @param ctx
     */
    protected abstract void process(RequestContent ctx);
}

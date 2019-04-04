package com.osidocker.open.micro.draw.system.resources;
import com.osidocker.open.micro.draw.system.concurrent.AtomicEntity;
import com.osidocker.open.micro.draw.system.transfer.DrawRequestContext;
import com.osidocker.open.micro.lock.ILockResource;
import com.osidocker.open.micro.lock.LocalLockResource;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @Description:    //TODO 单机环境可以使用,多机环境会有问题
 * @author: caoyj
 * @date: 2019年03月12日 8:45
 * @Copyright: © Caoyj
 */
public abstract class AbstractResourceLoadLocal<RequestContent extends DrawRequestContext, Source extends AtomicEntity> extends AbstractResourceLoad<RequestContent, Source> {

    private volatile ILockResource lockResource;
    /**
     * 加载后的资源内容
     */
    protected ConcurrentHashMap<String,Source> resourceMap = new ConcurrentHashMap<String,Source>(2048);

    @Override
    protected boolean containsResource(RequestContent ctx) {
        return isForceRefresh()?false:resourceMap.containsKey( resourceName(ctx) );
    }

    @Override
    protected Source getSource(RequestContent ctx) {
        return resourceMap.get(resourceName(ctx));
    }

    @Override
    public void close(){
        resourceMap.clear();
        resourceMap = null;
    }

    @Override
    protected synchronized ILockResource getLockResource() {
        if( lockResource==null ){
            lockResource = new LocalLockResource();
        }
        return lockResource;
    }

    public ConcurrentHashMap<String, Source> getResourceMap() {
        return resourceMap;
    }
}

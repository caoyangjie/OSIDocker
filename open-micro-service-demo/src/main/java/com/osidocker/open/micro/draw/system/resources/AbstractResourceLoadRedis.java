package com.osidocker.open.micro.draw.system.resources;

import com.osidocker.open.micro.draw.system.transfer.DrawRequestContext;
import com.osidocker.open.micro.lock.ILockResource;

/**
 * @Description:        //TODO 待实现的redis资源获取抽象类
 * @author: caoyj
 * @date: 2019年03月13日 8:43
 * @Copyright: © 麓山云
 */
public abstract class AbstractResourceLoadRedis<RequestContent extends DrawRequestContext,Source> extends AbstractResourceLoad<RequestContent,Source> {

    @Override
    public String resourceName(RequestContent ctx) {
        return null;
    }

    @Override
    public ILockResource getLockResource() {
        return null;
    }
}

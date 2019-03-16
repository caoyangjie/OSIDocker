package com.osidocker.open.micro.draw.system.validate;

import com.osidocker.open.micro.draw.service.IActiveTypeService;
import com.osidocker.open.micro.draw.service.impl.ActiveTypeServiceImpl;
import com.osidocker.open.micro.draw.system.AbstractCheckHandler;
import com.osidocker.open.micro.draw.system.factory.DrawProcessCacheKeyFactory;
import com.osidocker.open.micro.draw.system.transfer.DrawRequestContext;
import com.osidocker.open.micro.spring.SpringContextHolder;
import com.osidocker.open.micro.vo.CoreException;

/**
 * @Description:
 * @author: caoyj
 * @date: 2019年03月11日 16:42
 * @Copyright: © 麓山云
 */
public class ActiveTypeCheck extends AbstractCheckHandler<DrawRequestContext> {

    @Override
    protected boolean validate(DrawRequestContext ctx) throws CoreException {
        String key = DrawProcessCacheKeyFactory.getActiveTypeKey(getActiveId(ctx),getActiveTypeId(ctx));
        if( ctx.getProcessCacheData().getOrDefault( key,null) == null){
            ctx.getProcessCacheData().putIfAbsent( key, getActiveTypeService().getActiveTypeBy(getActiveId(ctx),getActiveTypeId(ctx)) );
        }
        return true;
    }


    private IActiveTypeService getActiveTypeService(){
        return SpringContextHolder.getBean(ActiveTypeServiceImpl.ACTIVE_TYPE_SERVICE_IMPL);
    }
}

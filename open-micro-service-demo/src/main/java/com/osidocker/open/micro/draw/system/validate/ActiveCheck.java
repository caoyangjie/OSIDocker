package com.osidocker.open.micro.draw.system.validate;

import com.osidocker.open.micro.draw.model.Active;
import com.osidocker.open.micro.draw.service.IActiveService;
import com.osidocker.open.micro.draw.service.impl.ActiveServiceImpl;
import com.osidocker.open.micro.draw.system.AbstractCheckHandler;
import com.osidocker.open.micro.draw.system.GunsCheckException;
import com.osidocker.open.micro.draw.system.factory.DrawProcessCacheKeyFactory;
import com.osidocker.open.micro.draw.system.transfer.DrawRequestContext;
import com.osidocker.open.micro.spring.SpringContextHolder;
import com.osidocker.open.micro.vo.CoreException;

import java.util.Map;
import java.util.Optional;

/**
 * @Description:    校验请求 活动Id是否存在
 * @author: caoyj
 * @date: 2019年03月11日 16:02
 * @Copyright: © 麓山云
 */
public class ActiveCheck extends AbstractCheckHandler<DrawRequestContext> {

    @Override
    protected boolean validate(DrawRequestContext ctx) throws CoreException {
        Optional<Map> mapOpt = Optional.ofNullable(ctx.getTransData());
        String key = DrawProcessCacheKeyFactory.getActiveKey( getActiveId(ctx) );
        if( ctx.getProcessCacheData().getOrDefault( key,null ) == null ){
            Active active = Optional.ofNullable(getActiveService().selectById(getActiveId(ctx))).orElseThrow(()->new CoreException(GunsCheckException.CheckExceptionEnum.ACTIVE_IS_NOT_EXIST));
            ctx.getProcessCacheData().put( key, active );
        }
        return true;
    }

    private IActiveService getActiveService(){
        return SpringContextHolder.getBean(ActiveServiceImpl.ACTIVE_SERVICE_IMPL);
    }
}

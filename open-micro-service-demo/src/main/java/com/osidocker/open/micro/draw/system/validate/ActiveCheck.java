package com.osidocker.open.micro.draw.system.validate;

import com.osidocker.open.micro.draw.model.Active;
import com.osidocker.open.micro.draw.service.IActiveService;
import com.osidocker.open.micro.draw.service.impl.ActiveServiceImpl;
import com.osidocker.open.micro.draw.system.AbstractCheckHandler;
import com.osidocker.open.micro.draw.system.CoreCheckException;
import com.osidocker.open.micro.draw.system.transfer.DrawRequestContext;
import com.osidocker.open.micro.spring.SpringContextHolder;
import com.osidocker.open.micro.vo.CoreException;

import java.util.Optional;

/**
 * @Description:    校验请求 活动Id是否存在
 * @author: caoyj
 * @date: 2019年03月11日 16:02
 * @Copyright: © Caoyj
 */
public class ActiveCheck extends AbstractCheckHandler<DrawRequestContext> {

    @Override
    protected boolean validate(DrawRequestContext ctx) throws CoreException {
        Active active = Optional.ofNullable(getActiveService().findById(getActiveId(ctx))).orElseThrow(()->new CoreException(CoreCheckException.CheckExceptionEnum.ACTIVE_IS_NOT_EXIST));
        if( active.getStatus() == 0 ){
            throw new CoreException(CoreCheckException.CheckExceptionEnum.NOT_PUBLISH_ACTIVE);
        }
        return true;
    }

    private IActiveService getActiveService(){
        return SpringContextHolder.getBean(ActiveServiceImpl.ACTIVE_SERVICE_IMPL);
    }
}

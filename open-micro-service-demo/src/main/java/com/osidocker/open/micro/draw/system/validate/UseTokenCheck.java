package com.osidocker.open.micro.draw.system.validate;


import com.osidocker.open.micro.draw.model.ActiveType;
import com.osidocker.open.micro.draw.system.GunsCheckException;
import com.osidocker.open.micro.draw.system.factory.DrawProcessCacheKeyFactory;
import com.osidocker.open.micro.draw.system.transfer.DrawRequestContext;
import com.osidocker.open.micro.vo.CoreException;

import java.util.Optional;

/**
 * @Description:
 * @author: caoyj
 * @date: 2019年03月11日 12:32
 * @Copyright: © 麓山云
 */
public class UseTokenCheck extends ActiveTypeCheck {

    @Override
    protected boolean validate(DrawRequestContext ctx) throws CoreException {
        super.validate(ctx);
        //没有使用口令,直接通过验证
        if( !ctx.isUseTokenFlag() ){
            return true;
        }
        if( checkToken(ctx) ){
            return true;
        }
        throw new CoreException(GunsCheckException.CheckExceptionEnum.USE_TOKEN_CHECK);
    }

    protected boolean checkToken(DrawRequestContext ctx) throws CoreException{
        String key = DrawProcessCacheKeyFactory.getActiveTypeKey(getActiveId(ctx),getActiveTypeId(ctx));
        ActiveType type = (ActiveType) ctx.getProcessCacheData().get(key);
        if(Optional.ofNullable(ctx.getToken()).isPresent() ){
            return ctx.getToken().equalsIgnoreCase(type.getPassword());
        }
        return false;
    }
}

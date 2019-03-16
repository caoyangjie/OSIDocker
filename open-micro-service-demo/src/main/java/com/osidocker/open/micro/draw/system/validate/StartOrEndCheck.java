package com.osidocker.open.micro.draw.system.validate;

import com.osidocker.open.micro.draw.system.AbstractCheckHandler;
import com.osidocker.open.micro.draw.system.GunsCheckException;
import com.osidocker.open.micro.draw.system.transfer.DrawRequestContext;
import com.osidocker.open.micro.vo.CoreException;

import java.util.Date;
import java.util.Optional;

/**
 * @Description:
 * @author: caoyj
 * @date: 2019年03月11日 13:09
 * @Copyright: © 麓山云
 */
public class StartOrEndCheck extends AbstractCheckHandler<DrawRequestContext> {

    @Override
    protected boolean validate(DrawRequestContext ctx) throws CoreException {
        Optional startOpt = Optional.ofNullable(ctx.getTransData().get("start"));
        Optional endOpt = Optional.ofNullable(ctx.getTransData().get("end"));
        if( startOpt.isPresent() ){
            Date start = (Date) startOpt.get();
            if( start.after(new Date()) ){
                throw new CoreException(GunsCheckException.CheckExceptionEnum.START_CHECK_ERROR);
            }
        }
        if( endOpt.isPresent() ){
            Date end = (Date) endOpt.get();
            if( end.before(new Date()) ){
                throw new CoreException(GunsCheckException.CheckExceptionEnum.END_CHECK_ERROR);
            }
        }
        return true;
    }
}

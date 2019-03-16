package com.osidocker.open.micro.draw.system.validate;


import com.osidocker.open.micro.draw.system.AbstractCheckHandler;
import com.osidocker.open.micro.draw.system.GunsCheckException;
import com.osidocker.open.micro.draw.system.factory.DrawConstantFactory;
import com.osidocker.open.micro.draw.system.transfer.DrawRequestContext;
import com.osidocker.open.micro.vo.CoreException;

/**
 * @Description:
 * @author: caoyj
 * @date: 2019年03月12日 13:50
 * @Copyright: © 麓山云
 */
public class SignCheck extends AbstractCheckHandler<DrawRequestContext> {

    @Override
    protected boolean validate(DrawRequestContext ctx) throws CoreException {
        //TODO 执行签名验证校验
        if( ctx.getTransData().getOrDefault(DrawConstantFactory.SIGN,-1).equals("88888888") ){
            return true;
        }
        throw new CoreException(GunsCheckException.CheckExceptionEnum.REQUEST_SIGN_IS_NOT_VALID);
    }
}

package com.osidocker.open.micro.draw.system.validate;

import com.osidocker.open.micro.draw.system.AbstractCheckHandler;
import com.osidocker.open.micro.draw.system.transfer.DrawRequestContext;
import com.osidocker.open.micro.vo.CoreException;

/**
 * @Description:
 * @author: caoyj
 * @date: 2019年03月11日 12:31
 * @Copyright: © 麓山云
 */
public class UserInfoCheck extends AbstractCheckHandler<DrawRequestContext> {

    @Override
    protected boolean validate(DrawRequestContext ctx) throws CoreException {
        return true;
    }
}

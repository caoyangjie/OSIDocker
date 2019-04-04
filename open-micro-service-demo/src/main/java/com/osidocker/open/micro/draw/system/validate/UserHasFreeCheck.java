package com.osidocker.open.micro.draw.system.validate;

import com.osidocker.open.micro.draw.system.AbstractCheckHandler;
import com.osidocker.open.micro.draw.system.transfer.DrawRequestContext;
import com.osidocker.open.micro.vo.CoreException;

/**
 * @Description:
 * @author: caoyj
 * @date: 2019年03月11日 15:38
 * @Copyright: © Caoyj
 */
public class UserHasFreeCheck extends AbstractCheckHandler<DrawRequestContext> {

    @Override
    protected boolean validate(DrawRequestContext ctx) throws CoreException {
        //TODO 用户可使用次数限制
        return true;
    }
}

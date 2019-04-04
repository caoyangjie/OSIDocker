package com.osidocker.open.micro.draw.system.validate;

import com.osidocker.open.micro.draw.system.AbstractCheckHandler;
import com.osidocker.open.micro.draw.system.CoreCheckException;
import com.osidocker.open.micro.draw.system.transfer.DrawRequestContext;
import com.osidocker.open.micro.security.vos.User;
import com.osidocker.open.micro.vo.CoreException;

/**
 * @Description:
 * @author: caoyj
 * @date: 2019年03月11日 12:31
 * @Copyright: © Caoyj
 */
public class UserInfoCheck extends AbstractCheckHandler<DrawRequestContext> {

    @Override
    protected boolean validate(DrawRequestContext ctx) throws CoreException {
        User user = new User();
        user.setUserId(ctx.getValueFormRequest("uid",-1).get().longValue());
        user.setUsername(ctx.getValueFormRequest("nickname","公安机关").get());
        ctx.setUser(user);

        if( user.getUserId() < 1 ){
            throw new CoreException(CoreCheckException.CheckExceptionEnum.USER_INFO_CHECK);
        }
        return true;
    }
}

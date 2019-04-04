package com.osidocker.open.micro.draw.system.validate;

import com.osidocker.open.micro.draw.system.AbstractCheckHandler;
import com.osidocker.open.micro.draw.system.CoreCheckException;
import com.osidocker.open.micro.draw.system.transfer.DrawRequestContext;
import com.osidocker.open.micro.vo.CoreException;

import java.util.HashSet;
import java.util.Set;

/**
 * @Description:
 * @author: caoyj
 * @date: 2019年03月29日 11:57
 * @Copyright: © Caoyj
 */
public class WhiteIpCheck extends AbstractCheckHandler<DrawRequestContext> {

    private static Set<String> whiteList = new HashSet();
    {
        whiteList.add("192.168.188.100");
    }

    @Override
    protected boolean validate(DrawRequestContext ctx) throws CoreException {
        boolean success = whiteList.contains(ctx.getIp());
        if( !success ){
            throw new CoreException(CoreCheckException.CheckExceptionEnum.NOT_ACCESS_IP_ADDRESS);
        }
        return true;
    }
}

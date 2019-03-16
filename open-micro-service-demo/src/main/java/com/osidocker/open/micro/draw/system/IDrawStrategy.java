package com.osidocker.open.micro.draw.system;

import com.osidocker.open.micro.vo.CoreException;

/**
 * @Description:
 * @author: caoyj
 * @date: 2019年03月11日 9:13
 * @Copyright: © 麓山云
 */
public interface IDrawStrategy<RequestCtx,ResponseCtx>  {

    /**
     * 根据请求进行对应的抽奖策略逻辑
     * @param ctx
     * @return                      返回中奖信息
     * @throws CoreException 抽奖外抛异常
     */
    ResponseCtx execute(RequestCtx ctx) throws CoreException;

}

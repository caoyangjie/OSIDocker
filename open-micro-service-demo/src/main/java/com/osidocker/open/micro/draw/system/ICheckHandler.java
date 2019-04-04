package com.osidocker.open.micro.draw.system;

/**
 * @Description:
 * @author: caoyj
 * @date: 2019年03月11日 9:05
 * @Copyright: © Caoyj
 */
public interface ICheckHandler<RequestContext> {

    /**
     * 逻辑程序是否继续执行的检测接口
     * @param ctx
     * @return
     * @throws CoreCheckException 外抛检查异常
     */
    boolean process(RequestContext ctx) throws CoreCheckException;

    /**
     * 设置规则检验器
     * @param handlers 注入规则检测器
     * @return
     */
    ICheckHandler setCheckHandlers(ICheckHandler... handlers);
}

package com.osidocker.open.micro.draw.system;

import com.osidocker.open.micro.draw.system.factory.DrawConstantFactory;
import com.osidocker.open.micro.draw.system.transfer.DrawRequestContext;
import com.osidocker.open.micro.vo.CoreException;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @Description:
 * @author: caoyj
 * @date: 2019年03月11日 10:25
 * @Copyright: © 麓山云
 */
public abstract class AbstractCheckHandler<RequestContext extends DrawRequestContext> implements ICheckHandler<RequestContext> {

    /**
     * 检测器的类的名称
     */
    private final String checkHandlerName = getClass().getSimpleName();
    /**
     * 检测器集合对象
     */
    private List<AbstractCheckHandler> checkHandlers;

    @Override
    public boolean process(RequestContext ctx) throws CoreException {
        Optional handlerOpt = Optional.ofNullable(checkHandlers);
        if( !Optional.ofNullable(ctx.getTransData()).isPresent() ){
            throw new CoreException(GunsCheckException.CheckExceptionEnum.TRANS_DATA_NOT_EXIST);
        }
        if( !validate(ctx) ){
            return false;
        }
        if( handlerOpt.isPresent() ){
            return Optional.ofNullable(checkHandlers).get()
                    .stream()
                    .allMatch(check->check.process(ctx));
        }
        return true;
    }

    /**
     * 添加判断规则
     * @param handlers
     */
    @Override
    public ICheckHandler setCheckHandlers(ICheckHandler... handlers){
        if(Optional.ofNullable(checkHandlers).isPresent()){
            checkHandlers.addAll(Arrays.asList(handlers).stream().flatMap(h-> Stream.of((AbstractCheckHandler)h)).collect(Collectors.toList()));
        }
        checkHandlers = Arrays.asList(handlers).stream().flatMap(h-> Stream.of((AbstractCheckHandler)h)).collect(Collectors.toList());
        return this;
    }

    protected Integer getActiveId(RequestContext ctx){
        return ctx.getValueFormRequest(DrawConstantFactory.ACTIVE_ID,-1).get();
    }

    protected Integer getActiveTypeId(RequestContext ctx){
        return ctx.getValueFormRequest(DrawConstantFactory.ACTIVE_TYPE_ID,-1).get();
    }
    /**
     * 执行校验逻辑
     * @param ctx   执行校验的上下文对象
     * @return
     * @throws CoreException 外抛检测错误异常
     */
    protected abstract boolean validate(RequestContext ctx) throws CoreException;
}

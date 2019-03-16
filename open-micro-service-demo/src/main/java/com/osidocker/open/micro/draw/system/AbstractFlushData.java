package com.osidocker.open.micro.draw.system;

import com.osidocker.open.micro.draw.system.factory.DrawConstantFactory;
import com.osidocker.open.micro.draw.system.transfer.AbstractContext;
import com.osidocker.open.micro.draw.system.transfer.DrawResponseContext;
import com.osidocker.open.micro.vo.CoreException;

import java.util.stream.Stream;

/**
 * @Description:
 * @author: caoyj
 * @date: 2019年03月15日 10:40
 * @Copyright: © 麓山云
 */
public abstract class AbstractFlushData implements IFlushDataToDb<DrawResponseContext> {

    @Override
    public boolean write(DrawResponseContext ctx) throws CoreException {
        //写入数据的时候进行参数校验
        if( ctx.isPrizeFlag() ){
            validate( ctx, Stream.of(DrawConstantFactory.ACTIVE_ID,DrawConstantFactory.ACTIVE_TYPE_ID,DrawConstantFactory.PRIZE_ID) );
            return normalFlushToDb(ctx);
        }else{
            //TODO 之前定时数据刷入数据库操作
            validate( ctx, normalArgsCheck() );
            return drawPrizeFlushToDb(ctx);
        }
    }

    /**
     * 检查请求参数是否在数据上下文中存在
     * @param ctx       数据上下文
     * @param fields    参数字段名数组
     * @return
     */
    protected final void validate(AbstractContext ctx, Stream<String> fields){
        if( !ctx.checkNotNull(fields) ){
            //TODO 记录日志,为何中奖后未能找到中奖数据
            throw new CoreException(GunsCheckException.CheckExceptionEnum.NOT_EXIST_ARGS);
        }
    }

    /**
     * 正常情况下的flushToDB
     * @param ctx   请求数据上下文对象
     * @return
     */
    protected abstract boolean normalFlushToDb(DrawResponseContext ctx);

    /**
     * 中奖情况下的flushToDb
     * @param ctx   请求数据上下文对象
     * @return
     */
    protected abstract boolean drawPrizeFlushToDb(DrawResponseContext ctx);

    /**
     * 正常刷新数据的校验参数名称数组
     * @return
     */
    protected abstract Stream<String> normalArgsCheck();
}

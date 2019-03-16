package com.osidocker.open.micro.draw.system;

import com.osidocker.open.micro.draw.system.factory.DrawPrizeProcessFactory;
import com.osidocker.open.micro.draw.system.factory.DrawStrategyFactory;
import com.osidocker.open.micro.draw.system.resources.AbstractResourceLoadLocal;
import com.osidocker.open.micro.draw.system.resources.local.LocalResourceActivePartakeDayCount;
import com.osidocker.open.micro.draw.system.resources.local.LocalResourceActivePrizeStatistics;
import com.osidocker.open.micro.draw.system.resources.local.LocalResourceSumAccessCount;
import com.osidocker.open.micro.draw.system.transfer.DrawRequestContext;
import com.osidocker.open.micro.draw.system.transfer.DrawResponseContext;
import com.osidocker.open.micro.spring.SpringContextHolder;
import com.osidocker.open.micro.vo.CoreException;
import com.osidocker.open.micro.vo.ServiceExceptionEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @Description:
 * @author: caoyj
 * @date: 2019年03月11日 9:21
 * @Copyright: © 麓山云
 */
public abstract class AbstractDrawStrategy<RequestCtx extends DrawRequestContext,ResponseCtx extends DrawResponseContext> implements IDrawStrategy<RequestCtx,ResponseCtx>{

    protected Logger logger = LoggerFactory.getLogger(this.getClass());
    /**
     * 存储当前抽奖策略的下一个抽奖策略
     */
    private AbstractDrawStrategy<RequestCtx,ResponseCtx> nextDraw;

    private ICheckHandler handler;

    @Override
    public ResponseCtx execute(RequestCtx ctx) throws CoreException {
        //判断是否为当前抽奖策略
        if( isThisStrategy(ctx) )
        {
            //判断所有检查规则是否通过
            if( handler.process(ctx) ){
                //执行 某个活动Id，活动类别的总访问次数
                incrementSumAccessCount(ctx);
                //执行 每日,每个活动Id,每个活动类别 对应的 访问计数
                incrementActivePartakeDayCount(ctx);
                //执行真实的抽奖逻辑
                ResponseCtx response = process(ctx);
                //执行中奖后的处理逻辑
                if( response.isPrizeFlag() ){
                    // 增加中奖次数
                    incrementActivePrizeStatistics(ctx);
                    //将中奖后的信息持久化到数据库中
                    flushDataToDb(response);
                }
            }else{
                throw new CoreException(new ServiceExceptionEnum() {
                    @Override
                    public Integer getCode() {
                        return 505;
                    }

                    @Override
                    public String getMessage() {
                        return "有规则未通过校验,但是没有外抛异常错误!";
                    }
                });
            }
        }
        return (ResponseCtx) Optional.ofNullable(next()).orElse(DrawStrategyFactory.NULL_DRAW_STRATEGY).execute(ctx);
    }


    /**
     * 添加某日,某个活动Id和活动类别的访问计数+1
     * @param ctx
     */
    private void incrementActivePartakeDayCount(DrawRequestContext ctx) {
        LocalResourceActivePartakeDayCount resource = (LocalResourceActivePartakeDayCount) getResourceLoad(LocalResourceActivePartakeDayCount.ACTIVE_PARTAKE_RESOURCE);
        resource.getResource(ctx).incrementAndGet();
    }

    /**
     * 添加某个活动和活动类别的总访问计数+1
     * @param ctx
     */
    private void incrementSumAccessCount(DrawRequestContext ctx){
        LocalResourceSumAccessCount sumAccessCount = (LocalResourceSumAccessCount) getResourceLoad(LocalResourceSumAccessCount.ACCESS_COUNT_RESOURCE);
        sumAccessCount.getResource(ctx).getInstance().incrementAndGet();
    }

    /**
     * 添加某个活动类别的奖品中奖次数计数+1
     * @param ctx
     */
    private void incrementActivePrizeStatistics(DrawRequestContext ctx){
        LocalResourceActivePrizeStatistics activePrizeStatistics = (LocalResourceActivePrizeStatistics) getResourceLoad(LocalResourceActivePrizeStatistics.ACTIVE_PARTAKE_STATISTICS_RESOURCE);
        activePrizeStatistics.getResource(ctx).incrementAndGet(ctx.getPrizeId());
    }

    public ResponseCtx fail(){
        return (ResponseCtx) new DrawResponseContext();
    }

    /**
     * 中奖后,强制执行的数据库持久代码
     * @return
     */
    protected boolean flushDataToDb(final ResponseCtx rsp){
        logger.info("强制执行中奖后的数据更新操作!");
        if( Optional.ofNullable(getRegisterResourceList()).isPresent() ){
            //TODO 添加一条新记录到 Active_Winning 表中
            //TODO 添加一条新记录到 Active_Users 表中
            //并发执行数据更新
            List<Future<Boolean>> writeResultList =
                    getRegisterResourceList()
                            .stream().flatMap(
                                    db-> Stream.of( DrawPrizeProcessFactory.flushDbPool.submit(()->db.write(rsp)) )
                    ).collect(Collectors.toList());
            return !writeResultList.stream().flatMap(booleanFuture -> {
                try {
                    return Stream.of(booleanFuture.get());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
                return Stream.of(false);
                //判断所有更新结果集是否更新成功
            }).allMatch(Boolean::booleanValue);
        }
        return false;
    }

    protected AbstractDrawStrategy<RequestCtx, ResponseCtx> next() {
        return nextDraw;
    }

    /**
     * 用来设置当前策略的下一抽奖策略
     * @param drawStrategy  下一个抽奖策略对象
     */
    public AbstractDrawStrategy setNextDraw(AbstractDrawStrategy<RequestCtx,ResponseCtx> drawStrategy){
        if( !Optional.ofNullable(drawStrategy).isPresent() ){
            this.nextDraw = DrawStrategyFactory.NULL_DRAW_STRATEGY;
            return DrawStrategyFactory.NULL_DRAW_STRATEGY;
        }
        if( this.nextDraw == null ){
            this.nextDraw = drawStrategy;
        }else{
            nextDraw.setNextDraw(drawStrategy);
        }
        return this;
    }

    public AbstractDrawStrategy setCheckHandler(ICheckHandler handler){
        this.handler = handler;
        return this;
    }

    /**
     *  进行真实的抽奖逻辑处理
     * @param ctx
     * @return
     * @throws CoreException 外抛抽奖处理错误异常
     */
    protected abstract ResponseCtx process(RequestCtx ctx) throws CoreException;

    /**
     * 注册抽奖服务相关的数据库持久资源提供服务对象
     * @return
     */
    private List<IFlushDataToDb<ResponseCtx>> getRegisterResourceList(){
        return (List<IFlushDataToDb<ResponseCtx>>) Stream.of(getResourceName()).flatMap(res-> Stream.of(SpringContextHolder.getBean(res)));
    }

    /**
     * 需要注入的资源服务对象的名称
     * @return
     */
    protected abstract String[] getResourceName();
    /**
     * 是否是当前抽奖策略对象
     * @param ctx 请求数据上下文
     * @return
     */
    protected boolean isThisStrategy(RequestCtx ctx){
        return false;
    }

    protected AbstractResourceLoadLocal getResourceLoad(String name){
        return (AbstractResourceLoadLocal) Optional.ofNullable(SpringContextHolder.getBean(name)).orElseThrow(()->new CoreException(GunsCheckException.CheckExceptionEnum.IOC_SERVICE_IS_NOT_FOUND));
    }
}

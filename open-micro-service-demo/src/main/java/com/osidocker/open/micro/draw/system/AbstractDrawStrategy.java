package com.osidocker.open.micro.draw.system;

import com.osidocker.open.micro.draw.service.IActiveUsersService;
import com.osidocker.open.micro.draw.service.IActiveWinningService;
import com.osidocker.open.micro.draw.system.impl.DefaultMessageQueue;
import com.osidocker.open.micro.draw.system.resources.AbstractResourceLoadLocal;
import com.osidocker.open.micro.draw.system.resources.local.LocalResourceActivePartakeDayCount;
import com.osidocker.open.micro.draw.system.resources.local.LocalResourceSumAccessCount;
import com.osidocker.open.micro.draw.system.transfer.DrawRequestContext;
import com.osidocker.open.micro.draw.system.transfer.DrawResponseContext;
import com.osidocker.open.micro.spring.SpringContextHolder;
import com.osidocker.open.micro.vo.CoreException;
import com.osidocker.open.micro.vo.ServiceExceptionEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @Description:
 * @author: caoyj
 * @date: 2019年03月11日 9:21
 * @Copyright: © Caoyj
 */
public abstract class AbstractDrawStrategy<RequestCtx extends DrawRequestContext,ResponseCtx extends DrawResponseContext> implements IDrawStrategy<RequestCtx,ResponseCtx>{

    protected Logger logger = LoggerFactory.getLogger(this.getClass());
    /**
     * 存储当前抽奖策略的下一个抽奖策略
     */
    private AbstractDrawStrategy<RequestCtx,ResponseCtx> nextDraw;

    private ICheckHandler handler;

    @Autowired
    protected IActiveWinningService winningService;

    @Autowired
    protected IActiveUsersService usersService;

    @Autowired
    @Qualifier(DefaultMessageQueue.DEFAULT_MESSAGE_QUEUE)
    protected IMessageQueue<DrawResponseContext> queue;

    @Override
    public ResponseCtx execute(RequestCtx ctx) throws CoreException {
        //如果是 DrawStrategyNull 实例对象,则说明没有中奖
        if( this instanceof DrawStrategyNull ){
            return null;
        }
        //判断是否为当前抽奖策略
        if( isThisStrategy(ctx) ){
            //判断所有检查规则是否通过
            if( handler.process(ctx) ){
                //执行 某个活动Id，活动类别的总访问次数
                incrementSumAccessCount(ctx);
                //执行 每日,每个活动Id,每个活动类别 对应的 访问计数
                incrementActivePartakeDayCount(ctx);
                //执行真实的抽奖逻辑
                ResponseCtx response;
                if( (response = process(ctx))!=null ){
                    return response;
                }
                //执行中奖后的处理逻辑
//                if( response!=null && response.isPrizeFlag() ){
//                    // 增加中奖次数
//                    incrementActivePrizeStatistics(ctx,response.getPrizeId());
//                    //将中奖后的信息持久化到数据库中
//                    flushDataToDb(response);
//                    return response;
//                }
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
        return (ResponseCtx) Optional.ofNullable(next()).orElse(new DrawStrategyNull()).execute(ctx);
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

    public ResponseCtx fail(){
        return (ResponseCtx) new DrawResponseContext();
    }

    /**
     * 中奖后,强制执行的数据库持久代码
     * @return
     */
    public boolean flushDataToDb(final ResponseCtx rsp){
        logger.info("强制执行中奖后的数据更新操作!");
        //是否添加数据库数据成功
        boolean success = insertDataToDb(rsp);
        if( !success ){
            //TODO 如果插入数据库失败,则进行日志登记,这里会报Json处理失败的错误
            queue.push(rsp);
        }
        if( Optional.ofNullable(getRegisterResourceList()).isPresent() && success ){
            //并发执行数据更新
            List<Boolean> writeResultList =
                    getRegisterResourceList()
                            .parallelStream().flatMap(
                                    db-> Stream.of(db.write(rsp) )
                    ).collect(Collectors.toList());
            return writeResultList.stream().allMatch(Boolean::booleanValue) && success;
        }
        return false;
    }

    /**
     * 将中奖信息持久化到数据库中
     * @param rsp
     * @return
     */
    protected abstract boolean insertDataToDb(ResponseCtx rsp);

    /**
     * Spring注入对象,构造函数执行成功之后执行的方法
     */
    @PostConstruct
    protected abstract void initCheckList();

    protected AbstractDrawStrategy<RequestCtx, ResponseCtx> next() {
        return nextDraw;
    }

    /**
     * 用来设置当前策略的下一抽奖策略
     * @param drawStrategy  下一个抽奖策略对象
     */
    public AbstractDrawStrategy setNextDraw(AbstractDrawStrategy<RequestCtx,ResponseCtx> drawStrategy){
        if( !Optional.ofNullable(drawStrategy).isPresent() ){
            throw new CoreException(CoreCheckException.CheckExceptionEnum.NOT_FIND_DRAW_STRATEGY);
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
    protected abstract List<IFlushDataToDb<ResponseCtx>> getRegisterResourceList();

    /**
     * 获取当前服务注入spring中的bean名称
     * @return
     */
    protected abstract String getBeanName();

    /**
     * 是否是当前抽奖策略对象
     * @param ctx 请求数据上下文
     * @return
     */
    protected abstract boolean isThisStrategy(RequestCtx ctx);

    protected AbstractResourceLoadLocal getResourceLoad(String name){
        return (AbstractResourceLoadLocal) Optional.ofNullable(SpringContextHolder.getBean(name)).orElseThrow(()->new CoreException(CoreCheckException.CheckExceptionEnum.IOC_SERVICE_IS_NOT_FOUND));
    }

    private static class DrawStrategyNull extends AbstractDrawStrategy{

        @Override
        protected boolean insertDataToDb(DrawResponseContext rsp) {
            return false;
        }

        @Override
        protected void initCheckList() {

        }

        @Override
        protected DrawResponseContext process(DrawRequestContext drawRequestContext) throws CoreException {
            return null;
        }

        @Override
        protected List<IFlushDataToDb> getRegisterResourceList() {
            return null;
        }

        @Override
        protected String getBeanName() {
            return null;
        }

        @Override
        protected boolean isThisStrategy(DrawRequestContext drawRequestContext) {
            return false;
        }

        @Override
        public Object execute(Object o) throws CoreException {
            throw new CoreException(CoreCheckException.CheckExceptionEnum.NOT_FIND_DRAW_STRATEGY);
        }
    }
}

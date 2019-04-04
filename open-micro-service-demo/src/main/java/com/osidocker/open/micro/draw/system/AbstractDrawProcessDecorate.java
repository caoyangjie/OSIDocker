package com.osidocker.open.micro.draw.system;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.osidocker.open.micro.draw.model.ActivePrize;
import com.osidocker.open.micro.draw.model.ActiveType;
import com.osidocker.open.micro.draw.model.ActiveUsers;
import com.osidocker.open.micro.draw.model.ActiveWinning;
import com.osidocker.open.micro.draw.service.IActivePrizeService;
import com.osidocker.open.micro.draw.service.IActiveTypeService;
import com.osidocker.open.micro.draw.service.IActiveUsersService;
import com.osidocker.open.micro.draw.service.impl.ActivePrizeServiceImpl;
import com.osidocker.open.micro.draw.service.impl.ActiveTypeServiceImpl;
import com.osidocker.open.micro.draw.service.impl.ActiveUsersServiceImpl;
import com.osidocker.open.micro.draw.system.concurrent.LocalAccessCount;
import com.osidocker.open.micro.draw.system.concurrent.LocalActivePrizeStatistics;
import com.osidocker.open.micro.draw.system.concurrent.LocalDayPartakeCount;
import com.osidocker.open.micro.draw.system.factory.DrawConstantFactory;
import com.osidocker.open.micro.draw.system.factory.DrawPrizeProcessFactory;
import com.osidocker.open.micro.draw.system.impl.CacheSuccessDataQueue;
import com.osidocker.open.micro.draw.system.resources.AbstractResourceLoadLocal;
import com.osidocker.open.micro.draw.system.resources.local.LocalResourceActivePartakeDayCount;
import com.osidocker.open.micro.draw.system.resources.local.LocalResourceActivePrizeStatistics;
import com.osidocker.open.micro.draw.system.resources.local.LocalResourceSumAccessCount;
import com.osidocker.open.micro.draw.system.transfer.DrawRequestContext;
import com.osidocker.open.micro.draw.system.transfer.DrawResponseContext;
import com.osidocker.open.micro.spring.SpringContextHolder;
import java.util.*;

/**
 * @Description:
 * @author: caoyj
 * @date: 2019年03月11日 17:26
 * @Copyright: © Caoyj
 */
public abstract class AbstractDrawProcessDecorate<RequestContext extends DrawRequestContext,ResponseContext extends DrawResponseContext> implements IDrawProcessDecorate<ResponseContext> {

    /**
     * 一个抽奖逻辑的扩展类对象
     */
    private AbstractDrawProcessDecorate<RequestContext,ResponseContext> next;
    private RequestContext ctx;

    /**
     * 本地线程数据上下文对象
     */
    protected ThreadLocal<RequestContext> requestContext = new ThreadLocal<RequestContext>(){
        @Override
        protected RequestContext initialValue() {
            return ctx;
        }
    };

    @Override
    public ResponseContext call() {
        //执行自己的抽奖逻辑
        try {
            ResponseContext responseContext = process();
            if (responseContext!=null) {
                return responseContext;
            } else {
                if (Optional.ofNullable(next).isPresent()) {
                    next.initRequestContent(requestContext.get());
                    return next.call();
                }
                return null;
            }
        }catch (Exception e){
            System.err.println(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }finally {
            releaseResource();
        }
    }

    public AbstractDrawProcessDecorate initRequestContent(RequestContext context){
        this.ctx = context;
        return this;
    }

    public AbstractDrawProcessDecorate setNext(AbstractDrawProcessDecorate<RequestContext, ResponseContext> next) {
        this.next = next;
        return this;
    }

    protected ActiveType getActiveType() {
        return getActiveTypeService().findById(requestContext.get().getActiveTypeId());
    }

    protected List<ActivePrize> getActivePrizeByType(){
        return getActivePrizeService().getPrizesByTypeId(requestContext.get().getActiveId(),requestContext.get().getActiveTypeId());
    }

    /**
     * 自定义实现的抽奖逻辑代码
     * @return
     */
    protected abstract ResponseContext process();

    protected void releaseResource(){
        requestContext.set(null);
    }

    private IActiveTypeService getActiveTypeService(){
        return SpringContextHolder.getBean(ActiveTypeServiceImpl.ACTIVE_TYPE_SERVICE_IMPL);
    }

    protected IActivePrizeService getActivePrizeService(){
        return SpringContextHolder.getBean(ActivePrizeServiceImpl.ACTIVE_PRIZE_SERVICE_IMPL);
    }

    private LocalDayPartakeCount getPartakeDayCount(){
        LocalResourceActivePartakeDayCount activePartakeDayCount = SpringContextHolder.getBean(LocalResourceActivePartakeDayCount.ACTIVE_PARTAKE_RESOURCE);
        return activePartakeDayCount.getResource(requestContext.get());
    }

    protected DrawResponseContext getSuccessPrize(ActiveType type,ActivePrize prize,String algorithmName){
        //校验今日中奖次数是否超过次数限制逻辑
        if (checkPrizeCountRule(prize)){
            return null;
        }
        //构建中奖返回对象
        return getDrawSuccessResponseContext(type, prize, algorithmName);
    }

    /**
     * 构建中奖返回对象
     * @param type
     * @param prize
     * @param algorithmName
     * @return
     */
    private DrawResponseContext getDrawSuccessResponseContext(ActiveType type, ActivePrize prize, String algorithmName) {
        // 增加中奖次数
        DrawPrizeProcessFactory.successAdd.increment();
        DrawResponseContext responseContext = new DrawResponseContext();
        responseContext.setRequestContext(requestContext.get());
        // 添加中奖统计视图信息
        statisticsInfo(responseContext,algorithmName,prize);
        //设置中奖标识
        responseContext.setPrizeFlag(true);
        Map<String,Object> transData = new HashMap<>();
        transData.put("prize_id", Optional.ofNullable(prize.getId()).orElse(-1));
        transData.put("prize_name",Optional.ofNullable(prize.getName()).orElse("奖品"));
        transData.put("prize_pic",Optional.ofNullable(prize.getPrizePic()).orElse(""));
        transData.put("class",Optional.ofNullable(type.getActiveId()).orElse(-1));
        transData.put("mold",Optional.ofNullable(prize.getMold()).orElse(-1));
        //TODO 获取中奖Id
        transData.put("win_id",-1);
        transData.put("sort",Optional.ofNullable(prize.getSort()).orElse(-1));
        transData.put("amount",Optional.ofNullable(prize.getAmount()).orElse(-1));
        if( prize.getMax()!=null && prize.getMax()>0 ){
            transData.put("angle",prize.getMax()-1);
        }
        transData.put("remark",Optional.ofNullable(prize.getRemark()).orElse(""));
        transData.put(DrawConstantFactory.INSERT_USER,getInsertActiveUsers(type));
        transData.put(DrawConstantFactory.INSERT_WINNING,getInsertActiveWinning(type,prize));
        responseContext.setBeanName(responseContext.getRequestContext().getBeanName());
        responseContext.getTransData().putAll(transData);
        // 异步处理中奖数据
        CacheSuccessDataQueue.getInstance().put(responseContext);
        return responseContext;
    }

    /**
     * 统计信息,用来观测抽奖算法的数据视图
     * @param responseContext       返回数据上下文对象
     * @param arithmeticName        抽奖算法名称
     * @param prize
     */
    protected void statisticsInfo(DrawResponseContext responseContext, String arithmeticName, ActivePrize prize){
        responseContext.setStatisMapVal(DrawConstantFactory.ARITHMETIC_NAME,arithmeticName);
        responseContext.setStatisMapVal(DrawConstantFactory.PARTAKE_DAY_COUNT,getPartakeDayCount().getVisit());
        responseContext.setStatisMapVal(DrawConstantFactory.SUM_ACCESS_COUNT,sumAccessCount().getInstance().get());
        responseContext.setStatisMapVal(DrawConstantFactory.USER_INFO,requestContext.get().getUser().getUsername());
        responseContext.setStatisMapVal(DrawConstantFactory.PRIZE_ID,prize.getId());
    }

    /**
     * 获取访问
     * @return
     */
    protected LocalAccessCount sumAccessCount(){
        return ((LocalResourceSumAccessCount)SpringContextHolder.getBean(LocalResourceSumAccessCount.ACCESS_COUNT_RESOURCE)).getResource(requestContext.get());
    }

    private ActiveUsers getInsertActiveUsers(ActiveType type) {
        //TODO 这里有个bug,同一个用户 同一个奖品中多次奖会体现不出来
        ActiveUsers activeUsers = getUserService().findByArgs(type.getId(),requestContext.get().getUser().getUserId().intValue(),type.getActiveId());
        if( activeUsers==null ){
            activeUsers = new ActiveUsers();
            activeUsers.setType(type.getId());
            activeUsers.setUid(requestContext.get().getUser().getUserId().intValue());
            activeUsers.setNickname(requestContext.get().getUser().getUsername());
            activeUsers.setClassId(type.getActiveId());
        }
        activeUsers.setIp(requestContext.get().getIp());
        activeUsers.setAddtime(new Date());
        return activeUsers;
    }

    private ActiveWinning getInsertActiveWinning(ActiveType type, ActivePrize prize){
        //TODO 这里有个bug,同一个用户 同一个奖品中多次奖会体现不出来
        ActiveWinning activeWinning = new ActiveWinning();
        activeWinning.setType(type.getId());
        activeWinning.setUid(requestContext.get().getUser().getUserId().intValue());
        activeWinning.setNickname(requestContext.get().getUser().getUsername());
        activeWinning.setPrizeId(prize.getId());
        activeWinning.setPrizeName(prize.getName());
        activeWinning.setPrizeType(prize.getMold());
        activeWinning.setPrizePic(prize.getPrizePic());
        activeWinning.setAmount(prize.getAmount());
        activeWinning.setStatus(0);
        activeWinning.setIsfree(-1);
        activeWinning.setWinTime(new Date());
        activeWinning.setIp(requestContext.get().getIp());
        return activeWinning;
    }

    private IActiveUsersService getUserService(){
        return SpringContextHolder.getBean(ActiveUsersServiceImpl.ACTIVE_USER_SERVICE_IMPL);
    }

    protected AbstractResourceLoadLocal<DrawRequestContext, LocalActivePrizeStatistics> statistics(){
        return SpringContextHolder.getBean(LocalResourceActivePrizeStatistics.ACTIVE_PARTAKE_STATISTICS_RESOURCE);
    }

    /**
     * 校验中奖逻辑
     * @param prize
     * @return
     */
    protected boolean checkPrizeCountRule(ActivePrize prize) {
        LocalActivePrizeStatistics statistics = statistics().getResource(requestContext.get());
        //这里使用 synchronized 同步处理,在高并发的情况下，这个代码块的代码会导致中奖次数变少
        //校验次数是否超标并增加今日中奖次数
        synchronized (prize){
            if( checkNum(prize.getDayNum()) && statistics.get(prize.getId()).intValue()<prize.getDayNum().intValue()? statistics.getAndIncrement(prize.getId()).intValue()> prize.getDayNum().intValue() : true ){
                return true;
            }
        }
        if( checkNum(prize.getWeekNum()) && statistics.countInWeek(prize.getId()).intValue() > prize.getWeekNum().intValue() ){
            statistics.decrementAndGet(prize.getId());
            return true;
        }
        if( checkNum(prize.getMonthNum()) && statistics.countInMouth(prize.getId()).intValue() > prize.getMonthNum().intValue() ){
            statistics.decrementAndGet(prize.getId());
            return true;
        }
        if( checkNum(prize.getTotalNum()) && statistics.countSum(prize.getId()).intValue() > prize.getTotalNum() ){
            statistics.decrementAndGet(prize.getId());
            return true;
        }
        return false;
    }

    private boolean checkNum(Integer num){
        return !(num==null || num < 0);
    }
}

package com.osidocker.open.micro.draw.system.resources.local;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.osidocker.open.micro.draw.model.ActivePrizeStatistics;
import com.osidocker.open.micro.draw.service.IActivePrizeStatisticsService;
import com.osidocker.open.micro.draw.service.impl.ActivePrizeStatisticsServiceImpl;
import com.osidocker.open.micro.draw.system.GunsCheckException;
import com.osidocker.open.micro.draw.system.concurrent.LocalActivePartakeStatistics;
import com.osidocker.open.micro.draw.system.concurrent.LocalProvideCount;
import com.osidocker.open.micro.draw.system.factory.DrawConstantFactory;
import com.osidocker.open.micro.draw.system.factory.DrawProcessCacheKeyFactory;
import com.osidocker.open.micro.draw.system.resources.AbstractResourceLoadLocal;
import com.osidocker.open.micro.draw.system.transfer.DrawRequestContext;
import com.osidocker.open.micro.draw.system.transfer.DrawResponseContext;
import com.osidocker.open.micro.spring.SpringContextHolder;
import com.osidocker.open.micro.utils.DateTimeKit;
import com.osidocker.open.micro.vo.CoreException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * @Description:    活动奖品中奖次数统计信息表
 * @author: caoyj
 * @date: 2019年03月14日 9:56
 * @Copyright: © 麓山云
 */
@Service(LocalResourceActivePrizeStatistics.ACTIVE_PARTAKE_STATISTICS_RESOURCE)
public class LocalResourceActivePrizeStatistics extends AbstractResourceLoadLocal<DrawRequestContext, LocalActivePartakeStatistics> {

    public static final String ACTIVE_PARTAKE_STATISTICS_RESOURCE = "localResourceActivePartakeStatistics";

    @Override
    protected String resourceName(DrawRequestContext ctx) {
        return DrawProcessCacheKeyFactory.getActivePrizeStatistic(ctx.getActiveId(),ctx.getActiveTypeId());
    }

    @Override
    protected void process(DrawRequestContext ctx) {
        String today = DateTimeKit.format(new Date(),DrawProcessCacheKeyFactory.YYYY_MM_DD);
        Optional<List<ActivePrizeStatistics>> apsOpt = Optional.ofNullable(getActivePrizeStatisticsService().selectList(getActivePrizeStatisticsEntityWrapper(ctx)));
        List<ActivePrizeStatistics> todayList = new ArrayList<>();
        //存在历史数据
        if( apsOpt.isPresent() ){
            todayList = apsOpt.get().stream().filter(p->p.getPartakeDate().equalsIgnoreCase(DateTimeKit.todayYMD())).collect(Collectors.toList());
            //但是不存在今天的数据
            if( todayList == null || todayList.isEmpty() ){
                todayList.addAll(initTodayActivePrizeStatistics(ctx, today));
            }
        }else{
            //不存在历史数据
            todayList.addAll(initTodayActivePrizeStatistics(ctx,today));
        }
        resourceMap.putIfAbsent(resourceName(ctx),
                new LocalActivePartakeStatistics(
                        //获取 activeId， activeTypeId
                        ctx.getActiveId(),ctx.getActiveTypeId(),
                        //实时统计  本周    今天之前的某个奖品中奖次数Map
                        activePartakeStatisticsSum(apsOpt,DateTimeKit::beforeTodayInThisWeek),
                        //实时统计  本月    今天之前的某个奖品中奖次数Map
                        activePartakeStatisticsSum(apsOpt,DateTimeKit::beforeTodayInThisMouth),
                        //保存      今日    统一活动Id，活动类别 下的 不同奖品的中奖次数
                        todayList.stream().filter(aps->today.equalsIgnoreCase(aps.getPartakeDate())).collect(Collectors.toList())
                )
        );
    }

    private List<ActivePrizeStatistics> initTodayActivePrizeStatistics(DrawRequestContext ctx, String today) {
        List<ActivePrizeStatistics> apsList = resource.getResource(ctx).getActivePrizeList().stream().flatMap(ap->{
            ActivePrizeStatistics aps = new ActivePrizeStatistics();
            aps.setClassId(ap.getClassId());
            aps.setPrize(ap.getId());
            aps.setPartakeDate( today );
            aps.setType(ap.getType());
            aps.setPrizeAccess(0);
            return Stream.of(aps);
        }).collect(Collectors.toList());
        if( !getActivePrizeStatisticsService().insertBatch(apsList) ){
            throw new CoreException(GunsCheckException.CheckExceptionEnum.INSERT_ACTIVE_PARTAKE_ERROR);
        }
        return apsList;
    }

    private EntityWrapper<ActivePrizeStatistics> getActivePrizeStatisticsEntityWrapper(DrawRequestContext ctx) {
        EntityWrapper<ActivePrizeStatistics> where = new EntityWrapper<>();
        where.eq(DrawConstantFactory.DB_CLASS,ctx.getActiveId())
                .eq(DrawConstantFactory.DB_TYPE,ctx.getActiveTypeId());
        return where;
    }

    /**
     * 根据请求表达式实时统计 中奖次数
     * @param apsOpt
     * @return
     */
    private Map<String,Integer> activePartakeStatisticsSum(Optional<List<ActivePrizeStatistics>> apsOpt, Predicate<String> predicate) {
         Map<String,List<ActivePrizeStatistics>> apsMap = apsOpt.get().stream()
                //符合本周统计条件
                 .filter(aps->predicate.test(aps.getPartakeDate()))
                .collect(
                        //将经过过滤后的list根据 活动Id，活动类别，奖品Id 进行 分组 存放到 Map中 结果为 Map<String,List>
                        Collectors.groupingBy(aps-> DrawProcessCacheKeyFactory.getActivePrizeStatistic(aps.getClassId(),aps.getType(),aps.getPrize()))
                );
         //将Map<String,List> 转换为 Map<String,Integer>
         return apsMap.keySet().stream().
                 collect(
                         Collectors.toMap(
                                 //转换后的 map中的key值： 还是 活动Id，活动类别，奖品id 为key
                                 k->k,
                                 //转换后的 map中的value值： 统计List中的对象的 prizeAccess 数量
                                 k->apsMap.get(k).stream().flatMapToInt(aps->IntStream.of(aps.getPrizeAccess())).sum()
                         )
                 );
    }

    @Override
    protected boolean normalFlushToDb(DrawResponseContext ctx) {
        return false;
    }

    @Override
    protected boolean drawPrizeFlushToDb(DrawResponseContext ctx) {
        LocalActivePartakeStatistics laps = resourceMap.get(resourceName(ctx.getRequestContext()));
        ActivePrizeStatistics aps = new ActivePrizeStatistics();
        aps.setId(laps.getInstance(ctx.getPrizeId().toString()).getId());
        aps.setPrizeAccess(laps.countInDay(ctx.getPrizeId()));
        return getActivePrizeStatisticsService().updateById(aps);
    }

    @Override
    protected Stream<String> normalArgsCheck() {
        return null;
    }

    private IActivePrizeStatisticsService getActivePrizeStatisticsService(){
        return SpringContextHolder.getBean(ActivePrizeStatisticsServiceImpl.ACTIVE_PRIZE_STATISTICS_SERVICE_IMPL);
    }

    @Autowired
    @Qualifier(LocalResourceActivePrize.PROVIDE_COUNT_LOCAL_RESOURCE)
    private AbstractResourceLoadLocal<DrawRequestContext, LocalProvideCount> resource;
}

package com.osidocker.open.micro.draw.system.concurrent;

import com.osidocker.open.micro.draw.model.ActivePrizeStatistics;
import com.osidocker.open.micro.draw.system.CoreCheckException;
import com.osidocker.open.micro.draw.system.factory.DrawProcessCacheKeyFactory;
import com.osidocker.open.micro.vo.CoreException;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @Description:
 * @author: caoyj
 * @date: 2019年03月14日 10:00
 * @Copyright: © Caoyj
 */
public class LocalActivePrizeStatistics extends AtomicEntity<ActivePrizeStatistics>{

    /**
     * 请求
     */
    private Map<String, ConcurrentActivePrizeStatistics> activePrizeStatMap;
    private Integer activeId;
    private Integer activeTypeId;
    private Map<String, Integer> mouthCount;
    private Map<String, Integer> weekCount;
    private Map<String, Integer> sumCount;

    public LocalActivePrizeStatistics(Integer activeId, Integer activeTypeId, Map<String,Integer> weekCount, Map<String,Integer> mouthCount, Map<String,Integer> sumCount, List<ActivePrizeStatistics> statistics){
        this.activeId = activeId;
        this.activeTypeId = activeTypeId;
        this.mouthCount = mouthCount;
        this.weekCount = weekCount;
        this.sumCount = sumCount;
        activePrizeStatMap = statistics.stream().flatMap(aps-> Stream.of(new ConcurrentActivePrizeStatistics(aps)))
                .collect(
                        Collectors.toMap(
                                aps-> DrawProcessCacheKeyFactory.getActivePrizeStatistic(activeId, activeTypeId,aps.getInstance().getPrize()), Function.identity()
                        )
                );
    }

    /**
     * 统计当前奖品今日活动中奖次数
     * @param prizeId
     * @return
     */
    public Integer countInDay(Integer prizeId){
        return activePrizeStatMap.get(key(prizeId)).getPrizeAccess();
    }

    /**
     * 统计活动,活动类别,奖品 本周中奖总次数
     * @param prizeId
     * @return
     */
    public Integer countInWeek(Integer prizeId){
        if( weekCount.containsKey(key(prizeId)) ){
            return weekCount.get(key(prizeId))+countInDay(prizeId);
        }else{
            return countInDay(prizeId);
        }
    }

    /**
     * 统计活动,活动类别，奖品 本月中奖总次数
     * @param prizeId
     * @return
     */
    public Integer countInMouth(Integer prizeId){
        if( mouthCount.containsKey(key(prizeId)) ){
            return mouthCount.get(key(prizeId))+countInDay(prizeId);
        }else{
            return countInDay(prizeId);
        }
    }

    public Integer countSum(Integer prizeId){
        if( sumCount.containsKey(key(prizeId)) ){
            return sumCount.get(key(prizeId))+countInDay(prizeId);
        }else{
            return countInDay(prizeId);
        }
    }

    /**
     * 针对参数Id的奖品的并发中奖更新次数
     * @param prizeId
     * @return
     */
    public Integer incrementAndGet(Integer prizeId){
        setChangeFlag(true);
        return activePrizeStatMap.get(key(prizeId)).incrementAndGet();
    }

    /**
     * 针对参数Id的奖品的并发中奖更新次数
     * @param prizeId
     * @return
     */
    public Integer getAndIncrement(Integer prizeId){
        setChangeFlag(true);
        return activePrizeStatMap.get(key(prizeId)).getAndIncrement();
    }

    public Integer decrementAndGet(Integer prizeId){
        setChangeFlag(true);
        return activePrizeStatMap.get(key(prizeId)).decrementAndGet();
    }

    public Integer get(Integer prizeId){
        return activePrizeStatMap.get(key(prizeId)).getPrizeAccess();
    }

    /**
     * 生成缓存key的私有方法
     * @param prizeId
     * @return
     */
    private String key(Integer prizeId){
        return DrawProcessCacheKeyFactory.getActivePrizeStatistic(activeId,activeTypeId,prizeId);
    }

    @Override
    public ActivePrizeStatistics getInstance(String... prizeId) {
        if( prizeId!=null && prizeId.length==1){
            return activePrizeStatMap.get(key(Integer.parseInt(prizeId[0]))).getInstance();
        }
        return null;
    }

    /**
     * ActivePrizeStatistics的并发内部处理类
     */
    private class ConcurrentActivePrizeStatistics extends ActivePrizeStatistics{

        /**
         * 并发更新对象
         */
        private volatile int access;
        /**
         * access 属性并发更新对象
         */
        private AtomicIntegerFieldUpdater<ConcurrentActivePrizeStatistics> accessUpdater = AtomicIntegerFieldUpdater.newUpdater(ConcurrentActivePrizeStatistics.class,"access");
        /**
         * 实际对象
         */
        private ActivePrizeStatistics instance;

        public ConcurrentActivePrizeStatistics(ActivePrizeStatistics instance) {
            accessUpdater.set(this,instance.getPrizeAccess());
            this.instance = instance;
        }

        @Override
        public Integer getPrizeAccess() {
            return accessUpdater.get(this);
        }

        public ActivePrizeStatistics getInstance(){
            instance.setPrizeAccess(getPrizeAccess());
            return instance;
        }

        @Override
        public void setPrizeAccess(Integer prizeAccess) {
            throw new CoreException(CoreCheckException.CheckExceptionEnum.CONCURRENT_PARTAKE_UN_SUPPORT_METHOD);
        }

        public Integer incrementAndGet(){
            return accessUpdater.incrementAndGet(this);
        }

        public Integer getAndIncrement(){
            return accessUpdater.getAndIncrement(this);
        }

        public Integer decrementAndGet(){
            return accessUpdater.decrementAndGet(this);
        }
    }
}

package com.osidocker.open.micro.draw.system.concurrent;

import com.osidocker.open.micro.draw.model.ActivePrize;
import com.osidocker.open.micro.draw.system.GunsCheckException;
import com.osidocker.open.micro.vo.CoreException;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @Description:
 * @author: caoyj
 * @date: 2019年03月13日 16:04
 * @Copyright: © 麓山云
 */
public class LocalProvideCount extends AtomicEntity<ActivePrize> {
    /**
     *  缓存key
     */
    private String cacheKey;

    /**
     * 奖品列表对应的缓存map
     */
    private Map<String, ConcurrentActivePrize> activePrizeMap;

    public LocalProvideCount(String cacheKey, List<ActivePrize> list){
        this.cacheKey = cacheKey;
        this.activePrizeMap = list.stream().collect(Collectors.toMap(prize->cacheKey+"@"+prize.getId(),prize->new ConcurrentActivePrize(prize)));
    }

    /**
     * 根据请求的活动Id，活动类型，活动奖品Id 获取 ConcurrentActivePrize 信息
     * @param prizeId       活动奖品Id
     * @return
     */
    private ConcurrentActivePrize getActivePrizeBy(Integer prizeId){
        return Optional.ofNullable(activePrizeMap.get(cacheKey+"@"+prizeId)).orElseThrow(()->new CoreException(GunsCheckException.CheckExceptionEnum.NOT_EXIST_ID));
    }

    /**
     * 获取数据库中奖品数量
     * @return
     */
    public List<ActivePrize> getActivePrizeList(){
        return activePrizeMap.values().stream().flatMap(val-> Stream.of(val.getInstance())).collect(Collectors.toList());
    }


    /**
     * 更新奖品Id对应的奖品的奖品发放次数
     * @param prizeId
     * @return
     */
    public Integer incrementAndGet(Integer prizeId){
        return getActivePrizeBy(prizeId).incrementAndGet();
    }

    /**
     * 根据请求的活动Id，活动类型，活动奖品Id 获取奖品信息
     * @param prizeId    奖品Id
     * @return
     */
    @Override
    public ActivePrize getInstance(String... prizeId) {
        if( prizeId!=null && prizeId.length==1){
            return getActivePrizeBy(Integer.parseInt(prizeId[0])).getInstance();
        }
        throw new CoreException(GunsCheckException.CheckExceptionEnum.NOT_EXIST_ARGS);
    }

    /**
     * 获取奖品Id对应的奖品发放次数
     * @param prizeId
     * @return
     */
    public Integer getOverNum(String... prizeId){
        return getActivePrizeBy(Integer.parseInt(prizeId[0])).getOverNum();
    }

    /**
     * 用来内部原子更新奖品排放数量的类
     */
    private class ConcurrentActivePrize extends ActivePrize{

        private AtomicIntegerFieldUpdater overNumIntUpdater = AtomicIntegerFieldUpdater.newUpdater(ConcurrentActivePrize.class,"dispatchPrizeCount");
        private ActivePrize prize;
        public volatile int dispatchPrizeCount;

        public Integer incrementAndGet(){
            return overNumIntUpdater.incrementAndGet(this);
        }

        public ConcurrentActivePrize(ActivePrize prize){
            overNumIntUpdater.set(this, prize.getOverNum());
            this.prize = prize;
        }

        public ActivePrize getInstance(){
            prize.setOverNum(getOverNum());
            return prize;
        }

        @Override
        public Integer getOverNum() {
            return overNumIntUpdater.get(this);
        }

        @Override
        public void setOverNum(Integer overNum) {
            throw new CoreException(GunsCheckException.CheckExceptionEnum.CONCURRENT_PARTAKE_UN_SUPPORT_METHOD);
        }
    }
}

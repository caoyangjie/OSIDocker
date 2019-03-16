package com.osidocker.open.micro.draw.system.factory;

import com.osidocker.open.micro.utils.DateTimeKit;

import java.util.Date;

/**
 * @Description:
 * @author: caoyj
 * @date: 2019年03月12日 11:54
 * @Copyright: © 麓山云
 */
public class DrawProcessCacheKeyFactory {
    public static final String ACTIVE = "active_";
    public static final String ACTIVE_TYPE = "activeType_";
    public static final String ACTIVE_PRIZE_LIST = "activePrizeList_";
    public static final String ACCESS_COUNT = "accessCount_";
    public static final String ACTIVE_PARTAKE = "activePartake_";
    public static final String YYYY_MM_DD = "yyyyMMdd";
    public static final String PROVIDE_COUNT = "provideCount_";
    public static final String ACTIVE_PRIZE_STATISTIC = "activePrizeStatistic_";

    /**
     * 获取ActiveType在处理缓存中的缓存key值
     * @param activeId
     * @param activeTypeId
     * @return
     */
    public static String getActiveTypeKey(Integer activeId, Integer activeTypeId) {
        return ACTIVE+activeId+":"+ ACTIVE_TYPE +activeTypeId;
    }

    /**
     * 获取Active在处理缓存中的缓存key值
     * @param activeId
     * @return
     */
    public static String getActiveKey(Integer activeId){
        return ACTIVE+activeId;
    }

    /**
     * 获取ActivePrize列表在缓存中的缓存key值
     * @param activeTypeId
     * @return
     */
    public static String getActivePrizeListByType(Integer activeId,Integer activeTypeId){
        return ACTIVE_PRIZE_LIST +activeId+":"+activeTypeId;
    }

    /**
     * 获取访问次数缓存key
     * @param activeId
     * @param activeTypeId
     * @return
     */
    public static String getAccessCountKey(Integer activeId,Integer activeTypeId){
        return ACCESS_COUNT +activeId+":"+ activeTypeId;
    }

    /**
     * 缓存当日访问次数的缓存Key
     * @return
     */
    public static String getActivePartakeKey(){
        return ACTIVE_PARTAKE + DateTimeKit.format(new Date(), YYYY_MM_DD);
    }

    /**
     * 缓存活动Id和活动类别下的活动奖品列表数据
     * @param activeId
     * @param activeTypeId
     * @return
     */
    public static String getProvideCountsById(Integer activeId,Integer activeTypeId){
        return PROVIDE_COUNT +activeId+ ":" +activeTypeId;
    }

    /**
     * 活动奖品中奖统计信息表
     * @param activeId          活动Id
     * @param activeTypeId      活动类别Id
     * @param prizeId           活动奖品Id
     * @return
     */
    public static String getActivePrizeStatistic(Integer activeId,Integer activeTypeId,Integer prizeId){
        return ACTIVE_PRIZE_STATISTIC +activeId+":"+activeTypeId+":"+prizeId;
    }

    /**
     * 活动奖品中奖统计信息表
     * @param activeId          活动Id
     * @param activeTypeId      活动类别Id
     * @return
     */
    public static String getActivePrizeStatistic(Integer activeId,Integer activeTypeId){
        return ACTIVE_PRIZE_STATISTIC +activeId+":"+activeTypeId;
    }
}

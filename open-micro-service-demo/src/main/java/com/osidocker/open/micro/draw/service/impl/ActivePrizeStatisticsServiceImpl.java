package com.osidocker.open.micro.draw.service.impl;

import com.osidocker.open.micro.draw.model.ActivePrizeStatistics;
import com.osidocker.open.micro.draw.mapper.ActivePrizeStatisticsMapper;
import com.osidocker.open.micro.draw.service.IActivePrizeStatisticsService;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 活动中奖次数统计信息表 服务实现类
 * </p>
 *
 * @author caoyj123
 * @since 2019-03-16
 */
@Service(ActivePrizeStatisticsServiceImpl.ACTIVE_PRIZE_STATISTICS_SERVICE_IMPL)
public class ActivePrizeStatisticsServiceImpl extends ServiceImpl<ActivePrizeStatisticsMapper, ActivePrizeStatistics> implements IActivePrizeStatisticsService {

    public static final String ACTIVE_PRIZE_STATISTICS_SERVICE_IMPL = "activePrizeStatisticsServiceImpl";
}

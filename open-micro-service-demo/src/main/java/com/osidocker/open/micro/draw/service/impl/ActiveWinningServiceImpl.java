package com.osidocker.open.micro.draw.service.impl;

import com.osidocker.open.micro.draw.model.ActiveWinning;
import com.osidocker.open.micro.draw.mapper.ActiveWinningMapper;
import com.osidocker.open.micro.draw.service.IActiveWinningService;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 获奖表 服务实现类
 * </p>
 *
 * @author caoyj123
 * @since 2019-03-16
 */
@Service(ActiveWinningServiceImpl.ACTIVE_WINNING_SERVICE_IMPL)
public class ActiveWinningServiceImpl extends ServiceImpl<ActiveWinningMapper, ActiveWinning> implements IActiveWinningService {

    public static final String ACTIVE_WINNING_SERVICE_IMPL = "activeWinningServiceImpl";
}

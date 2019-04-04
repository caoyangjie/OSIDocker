package com.osidocker.open.micro.draw.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.osidocker.open.micro.draw.mapper.ActiveWinningMapper;
import com.osidocker.open.micro.draw.model.ActiveWinning;
import com.osidocker.open.micro.draw.service.IActiveWinningService;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 获奖表 服务实现类
 * </p>
 *
 * @author stylefeng123
 * @since 2019-01-26
 */
@Service(ActiveWinningServiceImpl.ACTIVE_WINNING_SERVICE_IMPL)
public class ActiveWinningServiceImpl extends ServiceImpl<ActiveWinningMapper, ActiveWinning> implements IActiveWinningService {

    public static final String ACTIVE_WINNING_SERVICE_IMPL = "activeWinningServiceImpl";

    @Override
    public List<Map<String, Object>> list(String condition, String active_id) {
        return this.baseMapper.list(condition,active_id);
    }
}

package com.osidocker.open.micro.draw.service.impl;

import com.osidocker.open.micro.draw.model.Active;
import com.osidocker.open.micro.draw.mapper.ActiveMapper;
import com.osidocker.open.micro.draw.service.IActiveService;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 抽奖活动列表 服务实现类
 * </p>
 *
 * @author caoyj123
 * @since 2019-03-16
 */
@Service(ActiveServiceImpl.ACTIVE_SERVICE_IMPL)
public class ActiveServiceImpl extends ServiceImpl<ActiveMapper, Active> implements IActiveService {

    public static final String ACTIVE_SERVICE_IMPL = "activeServiceImpl";
}

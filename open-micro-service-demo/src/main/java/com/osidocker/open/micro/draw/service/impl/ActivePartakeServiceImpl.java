package com.osidocker.open.micro.draw.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.osidocker.open.micro.draw.mapper.ActivePartakeMapper;
import com.osidocker.open.micro.draw.model.ActivePartake;
import com.osidocker.open.micro.draw.service.IActivePartakeService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 活动参与次数表 服务实现类
 * </p>
 *
 * @author stylefeng123
 * @since 2019-01-26
 */
@Service(ActivePartakeServiceImpl.ACTIVE_PARTAKE_SERVICE_IMPL)
public class ActivePartakeServiceImpl extends ServiceImpl<ActivePartakeMapper, ActivePartake> implements IActivePartakeService {

    public static final String ACTIVE_PARTAKE_SERVICE_IMPL = "activePartakeServiceImpl";
}

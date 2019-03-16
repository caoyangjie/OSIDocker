package com.osidocker.open.micro.draw.service.impl;

import com.osidocker.open.micro.draw.model.ActiveUsers;
import com.osidocker.open.micro.draw.mapper.ActiveUsersMapper;
import com.osidocker.open.micro.draw.service.IActiveUsersService;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 参与用户表 服务实现类
 * </p>
 *
 * @author caoyj123
 * @since 2019-03-16
 */
@Service(ActiveUsersServiceImpl.ACTIVE_USERS_SERVICE_IMPL)
public class ActiveUsersServiceImpl extends ServiceImpl<ActiveUsersMapper, ActiveUsers> implements IActiveUsersService {

    public static final String ACTIVE_USERS_SERVICE_IMPL = "activeUsersServiceImpl";
}

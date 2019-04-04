package com.osidocker.open.micro.draw.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.osidocker.open.micro.draw.mapper.ActiveUsersMapper;
import com.osidocker.open.micro.draw.model.ActiveUsers;
import com.osidocker.open.micro.draw.service.IActiveUsersService;
import com.osidocker.open.micro.draw.service.IDrawCache;
import com.osidocker.open.micro.draw.system.factory.DrawConstantFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 参与用户表 服务实现类
 * </p>
 *
 * @author stylefeng123
 * @since 2019-03-28
 */
@Service(ActiveUsersServiceImpl.ACTIVE_USER_SERVICE_IMPL)
public class ActiveUsersServiceImpl extends ServiceImpl<ActiveUsersMapper, ActiveUsers> implements IActiveUsersService, IDrawCache {
    public static final String ACTIVE_USER_SERVICE_IMPL = "activeUserServiceImpl";

    @Override
    @Cacheable(cacheNames = EHCACHE_DRAW_CACHE+"_users",key = "'one_activeUsers_'+#uid+'#'+#type+'#'+#activeId")
    public ActiveUsers findByArgs(Integer type, Integer uid, Integer activeId) {
        EntityWrapper<ActiveUsers> where = new EntityWrapper<>();
        where.eq(DrawConstantFactory.DB_TYPE,type).eq(DrawConstantFactory.DB_UID,uid).eq(DrawConstantFactory.DB_CLASS,activeId);
        return selectOne(where);
    }
}

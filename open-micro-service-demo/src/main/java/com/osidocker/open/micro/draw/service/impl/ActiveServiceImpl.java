package com.osidocker.open.micro.draw.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.osidocker.open.micro.draw.mapper.ActiveMapper;
import com.osidocker.open.micro.draw.model.Active;
import com.osidocker.open.micro.draw.service.IActiveService;
import com.osidocker.open.micro.draw.service.IDrawCache;
import org.apache.ibatis.annotations.Param;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 抽奖活动列表 服务实现类
 * </p>
 *
 * @author stylefeng123
 * @since 2019-01-26
 */
@Service(ActiveServiceImpl.ACTIVE_SERVICE_IMPL)
public class ActiveServiceImpl extends ServiceImpl<ActiveMapper, Active> implements IActiveService, IDrawCache {

    public static final String ACTIVE_SERVICE_IMPL = "activeServiceImpl";

    @Override
    public List<Map<String, Object>> list(@Param("condition") String condition){
        return this.baseMapper.list(condition);
    }

    @Override
    @Cacheable(cacheNames = EHCACHE_DRAW_CACHE,key = "'one_active'+#id")
    public Active findById(Integer id) {
        return selectById(id);
    }
}

package com.osidocker.open.micro.draw.service.impl;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.osidocker.open.micro.draw.mapper.ActiveTypeMapper;
import com.osidocker.open.micro.draw.model.ActiveType;
import com.osidocker.open.micro.draw.service.IActiveTypeService;
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
 * @since 2019-03-28
 */
@Service(ActiveTypeServiceImpl.ACTIVE_TYPE_SERVICE_IMPL)
public class ActiveTypeServiceImpl extends ServiceImpl<ActiveTypeMapper, ActiveType> implements IActiveTypeService, IDrawCache {
    public static final String ACTIVE_TYPE_SERVICE_IMPL = "activeTypeServiceImpl";

    @Override
    public List<Map<String, Object>> getHdActiveTypeList(Page<ActiveType> page, String name, String active_id)
    {
        return this.baseMapper.getHdActiveTypeList(page,name,active_id);
    }

    @Override
    public List<Map<String, Object>> list(@Param("condition") String condition, @Param("active_id") String active_id){
        return this.baseMapper.list(condition,active_id);
    }

    @Override
    @Cacheable(cacheNames = EHCACHE_DRAW_CACHE,key = "'one_activeType_'+#id")
    public ActiveType findById(Integer id) {
        return this.selectById(id);
    }

}

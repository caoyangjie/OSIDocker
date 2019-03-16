package com.osidocker.open.micro.draw.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.osidocker.open.micro.draw.model.ActiveType;
import com.osidocker.open.micro.draw.mapper.ActiveTypeMapper;
import com.osidocker.open.micro.draw.service.IActiveTypeService;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.osidocker.open.micro.draw.system.GunsCheckException;
import com.osidocker.open.micro.vo.CoreException;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * <p>
 * 抽奖活动列表 服务实现类
 * </p>
 *
 * @author caoyj123
 * @since 2019-03-16
 */
@Service(ActiveTypeServiceImpl.ACTIVE_TYPE_SERVICE_IMPL)
public class ActiveTypeServiceImpl extends ServiceImpl<ActiveTypeMapper, ActiveType> implements IActiveTypeService {

    public static final String ACTIVE_TYPE_SERVICE_IMPL = "activeTypeServiceImpl";

    @Override
    public ActiveType getActiveTypeBy(Integer activeId, Integer activeTypeId) {
        EntityWrapper<ActiveType> where = new EntityWrapper<>();
        where.eq("id",activeTypeId).eq("active_id",activeId);
        ActiveType type = Optional.ofNullable(selectOne(where)).orElseThrow(()->new CoreException(GunsCheckException.CheckExceptionEnum.ACTIVE_TYPE_IS_NOT_EXIST));
        return type;
    }
}

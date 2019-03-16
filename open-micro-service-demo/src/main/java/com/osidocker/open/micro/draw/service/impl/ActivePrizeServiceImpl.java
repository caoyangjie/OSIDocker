package com.osidocker.open.micro.draw.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.osidocker.open.micro.draw.model.ActivePrize;
import com.osidocker.open.micro.draw.mapper.ActivePrizeMapper;
import com.osidocker.open.micro.draw.service.IActivePrizeService;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.osidocker.open.micro.draw.system.factory.DrawConstantFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 奖品设置表 服务实现类
 * </p>
 *
 * @author caoyj123
 * @since 2019-03-16
 */
@Service(ActivePrizeServiceImpl.ACTIVE_PRIZE_SERVICE_IMPL)
public class ActivePrizeServiceImpl extends ServiceImpl<ActivePrizeMapper, ActivePrize> implements IActivePrizeService {

    public static final String ACTIVE_PRIZE_SERVICE_IMPL = "activePrizeServiceImpl";

    @Override
    public List<ActivePrize> getPrizesByTypeId(Integer activeId, Integer activeTypeId) {
        //如果活动类别Id 小于等于0 则返回空对象w!
        if( activeId.intValue()<=0 || activeTypeId <= 0){
            return null;
        }
        EntityWrapper<ActivePrize> where = new EntityWrapper<>();
        where.eq(DrawConstantFactory.DB_CLASS,activeId).eq(DrawConstantFactory.DB_TYPE,activeTypeId);
        return selectList(where);
    }
}

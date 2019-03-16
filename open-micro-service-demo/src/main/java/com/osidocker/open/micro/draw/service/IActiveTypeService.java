package com.osidocker.open.micro.draw.service;

import com.osidocker.open.micro.draw.model.ActiveType;
import com.baomidou.mybatisplus.service.IService;

/**
 * <p>
 * 抽奖活动列表 服务类
 * </p>
 *
 * @author caoyj123
 * @since 2019-03-16
 */
public interface IActiveTypeService extends IService<ActiveType> {

    /**
     * 根据 活动Id 和 活动类别Id 获取请求对象
     * @param activeId      活动id
     * @param activeTypeId  活动类别Id
     * @return
     */
    ActiveType getActiveTypeBy(Integer activeId,Integer activeTypeId);
}

package com.osidocker.open.micro.draw.service;

import com.osidocker.open.micro.draw.model.ActivePrize;
import com.baomidou.mybatisplus.service.IService;

import java.util.List;

/**
 * <p>
 * 奖品设置表 服务类
 * </p>
 *
 * @author caoyj123
 * @since 2019-03-16
 */
public interface IActivePrizeService extends IService<ActivePrize> {


    List<ActivePrize> getPrizesByTypeId(Integer activeId, Integer activeTypeId);
}

package com.osidocker.open.micro.draw.service;

import com.baomidou.mybatisplus.service.IService;
import com.osidocker.open.micro.draw.model.ActivePrize;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 奖品设置表 服务类
 * </p>
 *
 * @author stylefeng123
 * @since 2019-01-26
 */
public interface IActivePrizeService extends IService<ActivePrize> {
    List<Map<String, Object>> list(@Param("condition") String conditiion, @Param("active_id") String active_id);

    List<ActivePrize> getPrizesByTypeId(Integer activeId, Integer activeTypeId);
}

package com.osidocker.open.micro.draw.service;

import com.baomidou.mybatisplus.service.IService;
import com.osidocker.open.micro.draw.model.Active;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 抽奖活动列表 服务类
 * </p>
 *
 * @author stylefeng123
 * @since 2019-01-26
 */
public interface IActiveService extends IService<Active> {
    List<Map<String, Object>> list(@Param("condition") String conditiion);
    Active findById(Integer id);
}

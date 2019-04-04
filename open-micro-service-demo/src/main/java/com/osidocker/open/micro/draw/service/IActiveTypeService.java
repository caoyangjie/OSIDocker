package com.osidocker.open.micro.draw.service;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;
import com.osidocker.open.micro.draw.model.ActiveType;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 抽奖活动列表 服务类
 * </p>
 *
 * @author stylefeng123
 * @since 2019-03-28
 */
public interface IActiveTypeService extends IService<ActiveType> {

    List<Map<String, Object>> getHdActiveTypeList(Page<ActiveType> page, String name, String active_id);
    List<Map<String, Object>> list(@Param("condition") String conditiion, @Param("active_id") String active_id);
    ActiveType findById(Integer id);
}

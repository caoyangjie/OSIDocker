package com.osidocker.open.micro.draw.mapper;

import com.baomidou.mybatisplus.plugins.Page;
import com.osidocker.open.micro.draw.model.ActiveType;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 抽奖活动列表 Mapper 接口
 * </p>
 *
 * @author stylefeng123
 * @since 2019-03-28
 */
public interface ActiveTypeMapper extends BaseMapper<ActiveType> {

    /**
     * 获取登录日志
     */
    List<Map<String, Object>> getHdActiveTypeList(@Param("page") Page<ActiveType> page, @Param("name") String name, @Param("active_id") String active_id);
    List<Map<String, Object>> list(@Param("condition") String conditiion, @Param("active_id") String active_id);
}

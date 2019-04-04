package com.osidocker.open.micro.draw.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.osidocker.open.micro.draw.model.Active;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 抽奖活动列表 Mapper 接口
 * </p>
 *
 * @author stylefeng123
 * @since 2019-01-26
 */
public interface ActiveMapper extends BaseMapper<Active> {
    List<Map<String, Object>> list(@Param("condition") String conditiion);
}

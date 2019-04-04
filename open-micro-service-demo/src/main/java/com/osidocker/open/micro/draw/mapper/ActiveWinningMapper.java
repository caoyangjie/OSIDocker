package com.osidocker.open.micro.draw.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.osidocker.open.micro.draw.model.ActiveWinning;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 获奖表 Mapper 接口
 * </p>
 *
 * @author stylefeng123
 * @since 2019-01-26
 */
public interface ActiveWinningMapper extends BaseMapper<ActiveWinning> {
    List<Map<String, Object>> list(@Param("condition") String conditiion, @Param("active_id") String active_id);
}

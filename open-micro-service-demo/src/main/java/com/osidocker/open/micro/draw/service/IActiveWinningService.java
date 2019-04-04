package com.osidocker.open.micro.draw.service;

import com.osidocker.open.micro.draw.model.ActiveWinning;
import com.baomidou.mybatisplus.service.IService;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 获奖表 服务类
 * </p>
 *
 * @author stylefeng123
 * @since 2019-01-26
 */
public interface IActiveWinningService extends IService<ActiveWinning> {
    List<Map<String, Object>> list(@Param("condition") String conditiion, @Param("active_id") String active_id);
}

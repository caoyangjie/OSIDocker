/**
 * ========================================================
 * <p>
 * <p>
 * <p>
 * <p>
 * ========================================================
 */
package com.osidocker.open.micro.pay.mapper;

import com.osidocker.open.micro.pay.entity.WeXinUserInfo;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

/**
 * @公司名称： 深圳原形信息技术有限公司
 * @类功能说明：
 * @类修改者： 杨浩
 * @创建日期： 创建于 2017/9/5 9:37
 * @修改说明：
 * @修改日期： 修改于 2017/9/5 9:37
 * @版本号： V1.0.0
 */
public interface WeXinMapper {
    Map<String,Object> getOpenId(@Param("userId") long userId);

    int addWeXinUser(@Param("map") Map<String, Object> map);

    int saveWeXinUserInfo(WeXinUserInfo userInfo);
}

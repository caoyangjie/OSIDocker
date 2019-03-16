/**
 * ===================================================================================
 * <p>
 * <p>
 * <p>
 * <p>
 * ===================================================================================
 */
package com.osidocker.open.micro.pay.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.osidocker.open.micro.pay.entity.SystemOrder;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

/**
 * @公司名称： 深圳原形信息技术有限公司
 * @类功能说明：
 * @类修改者： 曹杨杰
 * @author: Administrator
 * @创建日期： 创建于16:40 2017/12/15
 * @修改说明：
 * @修改日期： 修改于16:40 2017/12/15
 * @版本号： V1.0.0
 */
public interface OrderMapper extends BaseMapper<SystemOrder> {

    Map<String,Object> getOrderInfo(@Param("orderId") String orderId, @Param("status") String status);

    /**
     * 添加系统订单
     * @param order
     * @return
     */
    Long addSystemOrder(SystemOrder order);

    int updateOrderStatus(@Param("orderId") String orderId, @Param("status") String status,@Param("price")String payPrice);

    int orderFailed(String orderId);
}

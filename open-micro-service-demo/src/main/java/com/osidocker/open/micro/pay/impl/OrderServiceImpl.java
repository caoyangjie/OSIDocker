/**
 * ===================================================================================
 * <p>
 * <p>
 * <p>
 * <p>
 * ===================================================================================
 */
package com.osidocker.open.micro.pay.impl;

import com.osidocker.open.micro.pay.api.ApiOrderService;
import com.osidocker.open.micro.pay.entity.SystemOrder;
import com.osidocker.open.micro.pay.enums.PayStatusEnum;
import com.osidocker.open.micro.pay.enums.StatusEnum;
import com.osidocker.open.micro.pay.mapper.OrderMapper;
import com.osidocker.open.micro.pay.vos.ApiResponse;
import com.osidocker.open.micro.utils.StringUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Map;

/**
 * @公司名称： 深圳原形信息技术有限公司
 * @类功能说明：
 * @类修改者： 曹杨杰
 * @author: Administrator
 * @创建日期： 创建于11:52 2017/12/15
 * @修改说明：
 * @修改日期： 修改于11:52 2017/12/15
 * @版本号： V1.0.0
 */
@Service("orderService")
public class OrderServiceImpl extends BasePayService implements ApiOrderService{

    @Resource
    private OrderMapper orderMapper;

    @Override
    public ApiResponse getOrderInfo(String orderId) {
        Map<String,Object> map = orderMapper.getOrderInfo(orderId, null);
        if(!StringUtil.isEmpty(map)){
            return buildSuccMap(map);
        }
        return buildFail("无法获取订单信息!");
    }

    public ApiResponse createOrderInfo(String applyId,BigDecimal totalPrice){
        SystemOrder order = new SystemOrder();
        order.setApplyId(applyId);
        order.setTotalPrice(totalPrice);
        order.setOrderStatus(StatusEnum.getEnum(1).getDbValue());
        order.setPayStatus(PayStatusEnum.getEnum(3).getDbValue());
        orderMapper.addOrderInfo(order);
        return buildSuccess().initData(order);
    }

    @Override
    public Map<String, Object> getOrderInfo(String orderId, String status) {
        return orderMapper.getOrderInfo(orderId, status);
    }

    @Override
    public int updOrderStatus(String orderId, String status) {
        return orderMapper.updateOrderStatus(orderId,status);
    }

    @Override
    public int orderFailed(String orderId) {
        return orderMapper.orderFailed(orderId);
    }
}

/**
 * ==================================================================================
 * <p>
 * <p>
 * <p>
 * ==================================================================================
 */
package com.osidocker.open.micro.pay.impl;

import com.osidocker.open.micro.mapper.LoanApplyMapper;
import com.osidocker.open.micro.pay.api.ApiOrderService;
import com.osidocker.open.micro.pay.enums.OrderStatusEnums;
import com.osidocker.open.micro.pay.impl.BasePayService;
import com.osidocker.open.micro.pay.vos.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Map;

/**
 * @公司名称: 深圳原型信息技术有限公司
 * @类功能说明：
 * @类修改者： caoyangjie
 * @类作者： caoyangjie
 * @创建日期： 11:41 2018/9/17
 * @修改说明：
 * @修改日期： 11:41 2018/9/17
 * @版本号： V1.0.0
 */
@Service("orderServiceExtends")
public class OrderServiceExtendsImpl  extends BasePayService implements ApiOrderService {

    @Autowired
    @Qualifier("orderService")
    private ApiOrderService apiOrderService;

    @Resource
    private LoanApplyMapper loanApplyMapper;

    @Override
    public ApiResponse getOrderInfo(String orderId) {
        return apiOrderService.getOrderInfo(orderId);
    }

    @Override
    public ApiResponse createSystemOrder(String applyId, BigDecimal totalPrice) {
        return apiOrderService.createSystemOrder(applyId,totalPrice);
    }

    @Override
    public Map<String, Object> getOrderInfo(String orderId, String status) {
        return apiOrderService.getOrderInfo(orderId,status);
    }

    @Override
    public int updOrderStatus(String orderId, String status, String price) {
        if( OrderStatusEnums.SUCCESS.getStatus().equals(status) ){
            loanApplyMapper.updateLoanApplyStatus(loanApplyMapper.selectApplyId(orderId),"NEXT_HOME");
        }
        return apiOrderService.updOrderStatus(orderId,status,price);
    }

    @Override
    public int orderFailed(String orderId) {
        return apiOrderService.orderFailed(orderId);
    }
}

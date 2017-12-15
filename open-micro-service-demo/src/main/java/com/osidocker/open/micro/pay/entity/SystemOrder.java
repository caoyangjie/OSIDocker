/**
 * ========================================================
 * <p>
 * <p>
 * <p>
 * <p>
 * ========================================================
 */
package com.osidocker.open.micro.pay.entity;

import com.osidocker.open.micro.entity.CoreEntity;

import java.math.BigDecimal;

/**
 * @公司名称： 深圳原形信息技术有限公司
 * @类功能说明：
 * @类修改者： 杨浩
 * @author  caoyangjie
 * @创建日期： 创建于 2017/7/17 16:28
 * @修改说明：
 * @修改日期： 修改于 2017/7/17 16:28
 * @版本号： V1.0.0
 */
public class SystemOrder extends CoreEntity {
    private Long orderId;
    private String applyId;
    private BigDecimal totalPrice;
    private String orderStatus;
    private String payStatus;

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public void setApplyId(String applyId) {
        this.applyId = applyId;
    }

    public String getApplyId() {
        return applyId;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getPayStatus() {
        return payStatus;
    }

    public void setPayStatus(String payStatus) {
        this.payStatus = payStatus;
    }
}

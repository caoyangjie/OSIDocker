/**
 * ===================================================================================
 * <p>
 * <p>
 * <p>
 * <p>
 * ===================================================================================
 */
package com.osidocker.open.micro.pay.entity;

import com.osidocker.open.micro.entity.CoreEntity;
import com.osidocker.open.micro.pay.enums.OrderStatusEnums;

/**
 * @公司名称： 深圳原形信息技术有限公司
 * @类功能说明：
 * @类修改者： 曹杨杰
 * @author caoyangjie
 * @创建日期： 创建于10:47 2017/7/7
 * @修改说明：
 * @修改日期： 修改于10:47 2017/7/7
 * @版本号： V1.0.0
 */
public class PayOrder extends CoreEntity {
    /**
     * 支付Id
     */
    private Long id;
    /**
     * 关联id
     */
    private String applyId;
    /**
     * 商品名称
     */
    private String productName;
    /**
     * 第三方订单号
     */
    private String outTradeNo;
    /**
     * 订单Id
     */
    private String orderId;
    /**
     * 订单流水号
     */
    private String orderNo;
    /**
     * 订单金额
     */
    private String orderPrice;
    /**
     * 支付模式(支付宝、微信、国美支付)
     */
    private String payWayCode;
    /**
     * 支付类型(网站、手机、app)
     */
    private String payType;
    /**
     * 下单IP
     */
    private String orderIp;
    /**
     * 订单日期
     */
    private String orderDate;
    /**
     * 订单时间
     */
    private String orderTime;
    /**
     * 订单有效期
     */
    private Long orderPeriod;
    /**
     * 页面通知回调
     */
    private String returnUrl;
    /**
     * 后台消息通知
     */
    private String notifyUrl;
    /**
     * 支付备注
     */
    private String remark;
    /**
     * openId
     */
    private String openId;

    /**
     * 支付二维码地址
     */
    private String payCodeUrl;

    /**
     * 支付状态
     */
    private String status;
    /**
     * 备用字段
     */
    private String field1;
    /**
     * 备用字段
     */
    private String field2;
    /**
     * 备用字段
     */
    private String field3;
    /**
     * 备用字段
     */
    private String field4;
    /**
     * 备用字段
     */
    private String field5;

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public OrderStatusEnums getOrderStatus(){
        return OrderStatusEnums.getOrderStatus(this.getStatus());
    }

    public String getOutTradeNo() {
        return outTradeNo;
    }

    public void setOutTradeNo(String outTradeNo) {
        this.outTradeNo = outTradeNo;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getOrderPrice() {
        return orderPrice;
    }

    public void setOrderPrice(String orderPrice) {
        this.orderPrice = orderPrice;
    }

    public String getPayWayCode() {
        return payWayCode;
    }

    public void setPayWayCode(String payWayCode) {
        this.payWayCode = payWayCode;
    }

    public String getPayType() {
        return payType;
    }

    public void setPayType(String payType) {
        this.payType = payType;
    }

    public String getOrderIp() {
        return orderIp;
    }

    public void setOrderIp(String orderIp) {
        this.orderIp = orderIp;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public String getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(String orderTime) {
        this.orderTime = orderTime;
    }

    public Long getOrderPeriod() {
        return orderPeriod;
    }

    public void setOrderPeriod(Long orderPeriod) {
        this.orderPeriod = orderPeriod;
    }

    public String getReturnUrl() {
        return returnUrl;
    }

    public void setReturnUrl(String returnUrl) {
        this.returnUrl = returnUrl;
    }

    public String getNotifyUrl() {
        return notifyUrl;
    }

    public void setNotifyUrl(String notifyUrl) {
        this.notifyUrl = notifyUrl;
    }

    @Override
    public String getRemark() {
        return remark;
    }

    @Override
    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getApplyId() {
        return applyId;
    }

    public void setApplyId(String applyId) {
        this.applyId = applyId;
    }

    public String getPayCodeUrl() {
        return payCodeUrl;
    }

    public void setPayCodeUrl(String payCodeUrl) {
        this.payCodeUrl = payCodeUrl;
    }

    @Override
    public String getStatus() {
        return status;
    }

    @Override
    public void setStatus(String status) {
        this.status = status;
    }

    public String getField1() {
        return field1;
    }

    public void setField1(String field1) {
        this.field1 = field1;
    }

    public String getField2() {
        return field2;
    }

    public void setField2(String field2) {
        this.field2 = field2;
    }

    public String getField3() {
        return field3;
    }

    public void setField3(String field3) {
        this.field3 = field3;
    }

    public String getField4() {
        return field4;
    }

    public void setField4(String field4) {
        this.field4 = field4;
    }

    public String getField5() {
        return field5;
    }

    public void setField5(String field5) {
        this.field5 = field5;
    }
}

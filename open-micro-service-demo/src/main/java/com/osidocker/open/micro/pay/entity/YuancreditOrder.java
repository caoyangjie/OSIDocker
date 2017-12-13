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
 * @创建日期： 创建于10:47 2017/7/7
 * @修改说明：
 * @修改日期： 修改于10:47 2017/7/7
 * @版本号： V1.0.0
 */
public class YuancreditOrder extends CoreEntity {
    private String orderId;     //订单ID
    private String companyId;   //商户Id
    private String productName; //商品名称
    private String outTradeNo;  //第三方订单号
    private String orderNo;     //订单编号
    private String orderPrice;  //订单金额
    private String payWayCode;  //支付模式
    private String payType;     //支付类型
    private String orderIp;     //下单IP
    private String orderDate;   //下单日期
    private String orderTime;   //下单时间
    private Long orderPeriod;   //订单有效期
    private String returnUrl;   //页面通知回调
    private String notifyUrl;   //后台消息通知
    private String remark;      //支付备注
    private String openId;      //opendId
    private String field1;      //备用字段1
    private String field2;      //备用字段2
    private String field3;      //备用字段3
    private String field4;      //备用字段4
    private String field5;      //备用字段5

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
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

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
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

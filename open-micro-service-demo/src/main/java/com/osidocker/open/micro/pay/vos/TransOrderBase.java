/**
 * ===================================================================================
 * <p>
 * <p>
 * <p>
 * <p>
 * ===================================================================================
 */
package com.osidocker.open.micro.pay.vos;

import com.osidocker.open.micro.pay.exceptions.PayException;
import com.osidocker.open.micro.utils.StringUtil;

import java.io.Serializable;

/**
 * @公司名称： 深圳原形信息技术有限公司
 * @类功能说明：
 * @类修改者： 曹杨杰
 * @创建日期： 创建于17:49 2017/7/7
 * @修改说明：
 * @修改日期： 修改于17:49 2017/7/7
 * @版本号： V1.0.0
 */
public class TransOrderBase implements Serializable {
    private String productName; //商品名称
    private String orderId;     //订单编号
    private int payWayCode;  //支付模式
    private String orderIp;     //下单IP
    private String remark; // 备注
    private String payType; // 支付类型
    private String openId;  // 公众号支付需要传入openId

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getPayWayCode() {
        return payWayCode;
    }
    public void setPayWayCode(int payWayCode) {
        this.payWayCode = payWayCode;
    }

    public String getOrderIp() {
        return orderIp;
    }

    public void setOrderIp(String orderIp) {
        this.orderIp = orderIp;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getPayType() {
        return payType;
    }

    public void setPayType(String payType) {
        this.payType = payType;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public boolean checkTransData() {
        //TODO
        if(StringUtil.isEmpty(orderId)){
            throw new PayException("订单Id不允许为空!");
        }
        if(StringUtil.isEmpty(payWayCode)){
            throw new PayException("支付方式不允许为空!");
        }
        if (StringUtil.isEmpty(payType)){
            throw new PayException("支付类型不允许为空!");
        }
        return true;
    }
}

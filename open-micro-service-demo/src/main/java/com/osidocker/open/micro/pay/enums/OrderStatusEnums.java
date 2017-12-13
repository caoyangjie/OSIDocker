/**
 * ===================================================================================
 * <p>
 * <p>
 * <p>
 * <p>
 * ===================================================================================
 */
package com.osidocker.open.micro.pay.enums;

/**
 * @公司名称： 深圳原形信息技术有限公司
 * @类功能说明：
 * @类修改者： 曹杨杰
 * @创建日期： 创建于11:41 2017/7/7
 * @修改说明：
 * @修改日期： 修改于11:41 2017/7/7
 * @版本号： V1.0.0
 */
public enum OrderStatusEnums {
    INIT("init","订单创建"),
    NEEDPAY("not_pay","待支付"),
    CANCEL("cancel","交易关闭"),
    SUCCESS("pay_success","交易成功"),
    FAIL("pay_failure","交易失败");

    String status;
    String desc;
    OrderStatusEnums(String status,String desc){
        this.status = status;
        this.desc = desc;
    }

    public static OrderStatusEnums getOrderStatus(String status){
        for ( OrderStatusEnums order:OrderStatusEnums.values() ) {
            if(order.getStatus().equals(status)){
                return order;
            }
        }
        return null;
    }

    public String getStatus() {
        return status;
    }

    public String getDesc() {
        return desc;
    }
}

package com.osidocker.open.micro.pay.enums;

/**
 * Created by Administrator on 2017/3/15.
 */
public enum PayStatusEnum {
    NOTPAY(0,"not_pay","待支付"),
    PAYSUCCESS(1,"pay_success","支付成功"),
    PAYFAILURE(2,"pay_failure","支付失败"),
    INIT(3,"init","创建成功"),
    CANCEL(4,"cancel","取消订单"),
    UNKOWN(00,"unkown","未知状态");
    int data;//提交值
    String dbValue;//数据库保存值
    String desc;//值说明

    PayStatusEnum(int data, String dbValue, String desc)
    {
        this.data=data;
        this.dbValue=dbValue;
        this.desc = desc;
    }


    public String getDbValue() {
        return dbValue;
    }

    /**
     * 根据请求参数获取枚举类型
     * @param data 传入参数值
     * @return 返回枚举类型
     */
    public static PayStatusEnum getEnum(int data){
        PayStatusEnum[] ary = PayStatusEnum.values();
        for (int i = 0; i < ary.length; i++) {
            if(ary[i].data==data){
                return ary[i];
            }
        }
        return PayStatusEnum.UNKOWN;
    }
}

/**
 * ========================================================
 * <p>
 * <p>
 * <p>
 * <p>
 * ========================================================
 */
package com.osidocker.open.micro.pay.enums;

/**
 * @公司名称： 深圳原形信息技术有限公司
 * @类功能说明：
 * @类修改者： 杨浩
 * @创建日期： 创建于 2017/8/1 10:52
 * @修改说明：
 * @修改日期： 修改于 2017/8/1 10:52
 * @版本号： V1.0.0
 */
public enum PayTypeEnums {
    PC(1,"pc","电脑支付"),
    WEB(2,"wap","手机H5支付"),
    JSAPI(3,"jsapi","公众号"),
    UNKOWN(4,"unkown","未知状态");


    int data;//提交值
    String dbValue;//数据库保存值
    String desc;//值说明

    PayTypeEnums(int data, String dbValue, String desc)
    {
        this.data=data;
        this.dbValue=dbValue;
        this.desc = desc;
    }
    /**
     * 根据请求参数获取枚举类型
     * @param data 传入参数值
     * @return 返回枚举类型
     */
    public static PayTypeEnums getEnum(int data){
        PayTypeEnums[] ary = PayTypeEnums.values();
        for (int i = 0; i < ary.length; i++) {
            if(ary[i].data==data){
                return ary[i];
            }
        }
        return PayTypeEnums.UNKOWN;
    }

    public String getDbValue() {
        return dbValue;
    }
}

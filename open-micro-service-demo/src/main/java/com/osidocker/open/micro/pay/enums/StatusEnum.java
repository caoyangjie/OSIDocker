package com.osidocker.open.micro.pay.enums;

/**
 * Created by Administrator on 2017/3/15.
 */
public enum StatusEnum {
    INVALID(0,"invalid","无效"),
    VALID(1,"valid","有效"),
    DELETD(2,"delete","删除"),
    UNKOWN(4,"unkown","未知状态");

    int data;//提交值
    String dbValue;//数据库保存值
    String desc;//值说明

    StatusEnum(int data, String dbValue, String desc)
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
    public static StatusEnum getEnum(int data){
        StatusEnum[] ary = StatusEnum.values();
        for (int i = 0; i < ary.length; i++) {
            if(ary[i].data==data){
                return ary[i];
            }
        }
        return StatusEnum.UNKOWN;
    }
}

package com.osidocker.open.micro.pay.enums;

/**
 * Created by Administrator on 2017/3/15.
 */
public enum ScopeEnum {
    SNSAPI_BASE(1,"snsapi_base","基本信息"),
    SNSAPI_USERINFO(2,"snsapi_userinfo","详细信息"),
    UNKOWN(4,"unkown","未知状态");

    int data;//提交值
    String dbValue;//数据库保存值
    String desc;//值说明

    ScopeEnum(int data, String dbValue, String desc)
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
    public static ScopeEnum getEnum(int data){
        ScopeEnum[] ary = ScopeEnum.values();
        for (int i = 0; i < ary.length; i++) {
            if(ary[i].data==data){
                return ary[i];
            }
        }
        return ScopeEnum.UNKOWN;
    }

    public String getDbValue() {
        return dbValue;
    }
}

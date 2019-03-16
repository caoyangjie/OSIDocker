package com.osidocker.open.micro.draw.system.factory;

/**
 * @Description:
 * @author: caoyj
 * @date: 2019年03月12日 9:20
 * @Copyright: © 麓山云
 */
public class DrawConstantFactory {

    /**
     * 活动ID
     */
    public static final String ACTIVE_ID = "receive_type";
    /**
     * 活动类别ID
     */
    public static final String ACTIVE_TYPE_ID = "class";
    /**
     * 活动奖品Id
     */
    public static final String PRIZE_ID = "prize_id";
    /**
     * 用户ID
     */
    public static final String USER_ID = "uid";
    /**
     * 用户昵称
     */
    public static final String NICK_NAME = "nickname";
    /**
     * 签名字段
     */
    public static final String SIGN = "sign";


    /*******************************************    请求参数字段名 与 数据库字段名 分割线   **********************************************************************/

    /**
     * 活动类别ID 字段
     */
    public static final String DB_TYPE = "type";
    /**
     * 活动Id 字段
     */
    public static final String DB_CLASS = "class_id";
    /**
     * 活动中奖日期字段
     */
    public static final String DB_PRIZE_DATE = "partake_date";
    /**
     * 活动奖品Id
     */
    public static final String DB_PRIZE = "prize";
    /**
     * 数据库自增Id字段
     */
    public static final String DB_ID = "id";
    /**
     * 是否删除字段
     */
    public static final String DB_DEL = "del";
    /**
     * 日期字段
     */
    public static final String DB_DATE = "date";

    /******************************************   分隔符   ******************************************************************************************************/
    public static final String REGEX = ",";
}

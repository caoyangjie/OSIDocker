package com.osidocker.open.micro.draw.system.factory;

/**
 * @Description:
 * @author: caoyj
 * @date: 2019年03月12日 9:20
 * @Copyright: © Caoyj
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
    /**
     * 口令字段
     */
    public static final String TOKEN = "token";
    /**
     * 新增的ActiveUsers记录的key
     */
    public static final String INSERT_USER = "insert_user";
    /**
     * 新增的ActiveWinning记录的key
     */
    public static final String INSERT_WINNING = "insert_winning";
    /**
     * 抽奖算法名称字段
     */
    public static final String ARITHMETIC_NAME = "arithmeticName";
    /**
     * 抽奖逻辑执行次数字段
     */
    public static final String PARTAKE_DAY_COUNT = "partakeDayCount";
    /**
     * 中奖用户信息
     */
    public static final String USER_INFO = "userInfo";
    /**
     * 某个活动参与总次数
     */
    public static final String SUM_ACCESS_COUNT = "sumAccessCount";


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
     * 活动奖品Id
     */
    public static final String DB_PRIZE_ID = "prize_id";
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
    /**
     * 数据库用户id字段
     */
    public static final String DB_UID = "uid";

    /******************************************   分隔符   ******************************************************************************************************/
    public static final String REGEX = ",";
}

package com.osidocker.open.micro.draw.system;

import com.osidocker.open.micro.vo.CoreException;
import com.osidocker.open.micro.vo.ServiceExceptionEnum;

/**
 * @Description:
 * @author: caoyj
 * @date: 2019年03月11日 9:09
 * @Copyright: © 麓山云
 */
public class GunsCheckException extends CoreException {

    private GunsCheckException(ServiceExceptionEnum serviceExceptionEnum) {
        super(serviceExceptionEnum);
    }

    public static enum  CheckExceptionEnum implements ServiceExceptionEnum{
        /***/
        CONCURRENT_PARTAKE_UN_SUPPORT_METHOD(1001,"并发下的不支持set方法,请使用incrementAndGet!"),
        /***/
        IOC_SERVICE_IS_NOT_FOUND(801,"SpringIOC容器中未能找到当然资源名称的服务实例对象!"),
        /***/
        RUNTIME_REDIS_IS_NOT_SUPPORT(801,"当前运行环境不支持redis分布式更新"),
        /***/
        RUNTIME_ZOOKEEPER_IS_NOT_SUPPORT(801,"当前运行环境不支持zookeeper分布式更新"),
        /***/
        RUNTIME_UNKOWN_IS_NOT_SUPPORT(801,"当前运行环境不支持未知的分布式更新"),
        /***/
        INIT_DB_PRIZE_IS_NOT_EXIST(501,"当前活动的活动类别还未设置活动奖品!"),
        /***/
        NOT_EXIST_ID(501,"当前奖品Id未找到对应的数据!"),
        /***/
        NOT_EXIST_ARGS(501,"缺少请求参数!"),
        /***/
        LIMIT_TIMES_DAY(501,"当前活动每日中奖次数已达上线,不允许继续中奖!"),
        /***/
        LIMIT_TIMES_WEEK(501,"当前活动每周中奖次数已达上线,不允许继续中奖!"),
        /***/
        LIMIT_TIMES_MOUTH(50,"当前活动每月中奖次数已达上线,不允许继续中奖!"),
        /***/
        USER_INFO_CHECK(501,"参与抽奖必须进行用户登录,当前请求未能获取到用户信息!"),
        /***/
        USE_TOKEN_CHECK(501,"必须输入正确的口令才能参与抽奖活动!"),
        /***/
        START_CHECK_ERROR(501,"当前抽奖活动还没有开始!"),
        /***/
        END_CHECK_ERROR(501,"当前抽奖活动已经结束!"),
        /***/
        INSERT_ACTIVE_PARTAKE_ERROR(501,"创建activePartake对象失败!"),
        /***/
        INSERT_ACTIVE_PRIZE_STATISTICS(501,"初始化每日活动奖品中奖次数统计信息表报错!"),
        /***/
        TRANS_DATA_NOT_EXIST(901,"不存在请求数据上下文对象"),
        /***/
        REQUEST_SIGN_IS_NOT_VALID(901,"请求签名未通过校验!"),
        /***/
        ACTIVE_TYPE_IS_NOT_EXIST(901,"不存在的活动类型!"),
        /***/
        ACTIVE_PRIZE_IS_NOT_SET(901,"当前活动类别为设置活动奖品!"),
        /***/
        ACTIVE_IS_NOT_EXIST(901,"不存在活动Id!");

        private Integer code;

        private String message;

        CheckExceptionEnum(Integer code,String message){
            this.code = code;
            this.message = message;
        }

        @Override
        public Integer getCode() {
            return code;
        }

        @Override
        public String getMessage() {
            return message;
        }
    }
}

package com.osidocker.open.micro.draw.system.factory;

import com.osidocker.open.micro.draw.system.AbstractDrawStrategy;
import com.osidocker.open.micro.draw.system.impl.DrawPrizePartakeService;
import com.osidocker.open.micro.draw.system.transfer.DrawRequestContext;
import com.osidocker.open.micro.draw.system.transfer.DrawResponseContext;
import com.osidocker.open.micro.draw.system.validate.*;
import com.osidocker.open.micro.vo.CoreException;
import com.osidocker.open.micro.vo.ServiceExceptionEnum;

/**
 * @Description:
 * @author: caoyj
 * @date: 2019年03月11日 9:48
 * @Copyright: © 麓山云
 */
public class DrawStrategyFactory {

    /**
     * 如果没有找到抽奖策略,进行获取一个 空的抽奖策略
     */
    public static AbstractDrawStrategy NULL_DRAW_STRATEGY = new AbstractDrawStrategy<DrawRequestContext, DrawResponseContext>() {
        @Override
        public DrawResponseContext process(DrawRequestContext ctx) throws CoreException {
            if( this.equals(NULL_DRAW_STRATEGY) ){
                return null;
            }
            throw new CoreException(new ServiceExceptionEnum() {
                @Override
                public Integer getCode() {
                    return 501;
                }

                @Override
                public String getMessage() {
                    return "未能找到匹配的抽奖方式!";
                }
            });
        }

        @Override
        protected String[] getResourceName() {
            return new String[0];
        }

        @Override
        protected boolean isThisStrategy(DrawRequestContext o) {
            return true;
        }
    }.setCheckHandler(DrawCheckHandlerFactory.NULL_SUCCESS_HANDLER);

    /**
     *
     * @return
     */
    public static AbstractDrawStrategy buildBigWheel(){
        return new DrawPrizePartakeService()
                /**
                 *
                 */
                .setCheckHandler(
                        /**
                         *  初始化检查容器中的统一检查规则对象
                         */
                        DrawCheckHandlerFactory.defaultCheckContainer.setCheckHandlers(
                                //签名校验规则
                                new SignCheck(),
                                //活动开始结束限制规则
                                new StartOrEndCheck(),
                                //添加使用口令限制规则
                                new UseTokenCheck(),
                                //添加用户信息限制规则
                                new UserInfoCheck(),
                                //添加抽奖次数限制规则
                                new LimitTimesCheck(),
                                //添加用户免费抽奖次数
                                new UserHasFreeCheck(),
                                //添加活动Id是否存在检查
                                new ActiveCheck(),
                                //添加活动类型Id是否存在检查
                                new ActiveTypeCheck(),
                                //添加活动奖品是否存在的检查
                                new ActivePrizeCheck()
                        )
                );
    }

}

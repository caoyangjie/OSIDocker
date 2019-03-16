package com.osidocker.open.micro.draw.system.validate;

import com.osidocker.open.micro.draw.system.AbstractCheckHandler;
import com.osidocker.open.micro.draw.system.GunsCheckException;
import com.osidocker.open.micro.draw.system.IGenerateGunsException;
import com.osidocker.open.micro.draw.system.transfer.DrawRequestContext;
import com.osidocker.open.micro.vo.CoreException;

import java.util.Map;
import java.util.Optional;

/**
 * @Description:
 * @author: caoyj
 * @date: 2019年03月11日 11:04
 * @Copyright: © 麓山云
 */
public class LimitTimesCheck extends AbstractCheckHandler<DrawRequestContext> {

    public LimitTimesCheck(){
        init();
    }

    @Override
    public boolean validate(DrawRequestContext ctx) throws CoreException {
        return true;
    }

    public void init(){
        setCheckHandlers(
                limitDayCheck,
                limitWeekCheck,
                limitMouthCheck
        );
    }

    /**
     * 根据请求数据上下文,检查是否符合规则
     * @param ctx           上下文对象
     * @param fieldName     字段名称
     * @param handler       异常对象创建器
     * @return
     * @throws CoreException 外抛异常对象
     */
    private static boolean checkTransData(DrawRequestContext ctx, String fieldName, IGenerateGunsException handler) throws CoreException{
        Optional<Map> mapOpt = Optional.ofNullable(ctx.getTransData());
        if( mapOpt.isPresent() ){
            if( mapOpt.get().getOrDefault(fieldName,false).equals(true) ){
                throw handler.catchException();
            }
        }
        return true;
    }

    /**
     * 每日中奖次数限制
     */
    public static AbstractCheckHandler<DrawRequestContext> limitDayCheck = new AbstractCheckHandler<DrawRequestContext>() {

        @Override
        public boolean validate(DrawRequestContext ctx) throws CoreException {
            return checkTransData(ctx,"day_over",()->new CoreException(GunsCheckException.CheckExceptionEnum.LIMIT_TIMES_DAY));
        }
    };

    /**
     * 每周中奖次数限制
     */
    public static AbstractCheckHandler<DrawRequestContext> limitWeekCheck = new AbstractCheckHandler<DrawRequestContext>() {

        @Override
        public boolean validate(DrawRequestContext ctx) throws CoreException {
            return checkTransData(ctx,"week_over",()->new CoreException(GunsCheckException.CheckExceptionEnum.LIMIT_TIMES_WEEK));
        }
    };

    /**
     * 每月中奖次数限制
     */
    public static AbstractCheckHandler<DrawRequestContext> limitMouthCheck = new AbstractCheckHandler<DrawRequestContext>() {

        @Override
        public boolean validate(DrawRequestContext ctx) throws CoreException {
            return checkTransData(ctx,"mouth_over",()->new CoreException(GunsCheckException.CheckExceptionEnum.LIMIT_TIMES_MOUTH));
        }
    };
}

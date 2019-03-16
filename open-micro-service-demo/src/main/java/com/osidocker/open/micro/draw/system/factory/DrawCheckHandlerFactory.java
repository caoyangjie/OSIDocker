package com.osidocker.open.micro.draw.system.factory;

import com.osidocker.open.micro.draw.system.AbstractCheckHandler;
import com.osidocker.open.micro.draw.system.transfer.DrawRequestContext;
import com.osidocker.open.micro.vo.CoreException;
import com.osidocker.open.micro.vo.ServiceExceptionEnum;

/**
 * @Description:
 * @author: caoyj
 * @date: 2019年03月11日 10:15
 * @Copyright: © 麓山云
 */
public class DrawCheckHandlerFactory {

    /**
     * 添加一个默认外抛异常的规则检测器
     */
    public static AbstractCheckHandler<DrawRequestContext> NULL_EXCEPTION_HANDLER = new AbstractCheckHandler<DrawRequestContext>() {

        @Override
        public boolean validate(DrawRequestContext ctx) throws CoreException {
            if( this.equals(NULL_EXCEPTION_HANDLER) ){
                return true;
            }
            throw new CoreException(new ServiceExceptionEnum() {
                @Override
                public Integer getCode() {
                    return 501;
                }

                @Override
                public String getMessage() {
                    return "未注入对应的规则检测器!";
                }
            });
        }
    };

    /**
     * 添加一个默认成功的规则检测器
     */
    public static AbstractCheckHandler<DrawRequestContext> NULL_SUCCESS_HANDLER = new AbstractCheckHandler<DrawRequestContext>(){

        @Override
        public boolean validate(DrawRequestContext ctx) throws CoreException {
            return true;
        }
    };

    /**
     * 构建一个默认的 检测器容器对象
     */
    public static AbstractCheckHandler<DrawRequestContext> defaultCheckContainer = new AbstractCheckHandler<DrawRequestContext>() {

        @Override
        protected boolean validate(DrawRequestContext ctx) throws CoreException {
            return true;
        }
    };

}

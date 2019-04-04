package com.osidocker.open.micro.draw.system.validate;

import com.osidocker.open.micro.draw.model.ActiveType;
import com.osidocker.open.micro.draw.service.IActiveTypeService;
import com.osidocker.open.micro.draw.service.impl.ActiveTypeServiceImpl;
import com.osidocker.open.micro.draw.system.AbstractCheckHandler;
import com.osidocker.open.micro.draw.system.CoreCheckException;
import com.osidocker.open.micro.draw.system.factory.DrawConstantFactory;
import com.osidocker.open.micro.draw.system.transfer.DrawRequestContext;
import com.osidocker.open.micro.spring.SpringContextHolder;
import com.osidocker.open.micro.vo.CoreException;
import org.apache.commons.lang.StringUtils;

import java.util.Date;
import java.util.Optional;

/**
 * @Description:
 * @author: caoyj
 * @date: 2019年03月11日 16:42
 * @Copyright: © Caoyj
 */
public class ActiveTypeCheck extends AbstractCheckHandler<DrawRequestContext> {

    public ActiveTypeCheck() {
        init();
    }

    @Override
    protected boolean validate(DrawRequestContext ctx) throws CoreException {
//        String key = DrawProcessCacheKeyFactory.getActiveTypeKey(getActiveId(ctx),getActiveTypeId(ctx));
//        if( ctx.getProcessCacheData().getOrDefault( key,null) == null){
//            ActiveType type = Optional.ofNullable(getActiveTypeService().selectById(getActiveTypeId(ctx))).orElseThrow(()->new GunsException(CoreCheckException.CheckExceptionEnum.ACTIVE_TYPE_IS_NOT_EXIST));
//            ctx.getProcessCacheData().putIfAbsent( key, type );
//        }
        return true;
    }

    private void init() {
        setCheckHandlers(
            new StartOrEndCheck(),
            new UseTokenCheck()
        );
    }

    private IActiveTypeService getActiveTypeService(){
        return SpringContextHolder.getBean(ActiveTypeServiceImpl.ACTIVE_TYPE_SERVICE_IMPL);
    }

    private class StartOrEndCheck extends AbstractCheckHandler<DrawRequestContext> {

        @Override
        protected boolean validate(DrawRequestContext ctx) throws CoreException {
//            String key = DrawProcessCacheKeyFactory.getActiveTypeKey(getActiveId(ctx),getActiveTypeId(ctx));
//            ActiveType type = (ActiveType) ctx.getProcessCacheData().get(key);
            ActiveType type = getActiveType(ctx.getActiveTypeId());

            if( type.getIsTlimit() > 0 ){
                Optional startOpt = Optional.ofNullable(type.getStime());
                Optional endOpt = Optional.ofNullable(type.getEtime());
                if( startOpt.isPresent() ){
                    Date start = (Date) startOpt.get();
                    if( start.after(new Date()) ){
                        throw new CoreException(CoreCheckException.CheckExceptionEnum.START_CHECK_ERROR);
                    }
                }
                if( endOpt.isPresent() ){
                    Date end = (Date) endOpt.get();
                    if( end.before(new Date()) ){
                        throw new CoreException(CoreCheckException.CheckExceptionEnum.END_CHECK_ERROR);
                    }
                }
            }
            return true;
        }
    }

    private class UseTokenCheck extends AbstractCheckHandler<DrawRequestContext> {

        @Override
        protected boolean validate(DrawRequestContext ctx) throws CoreException {
            //没有使用口令,直接通过验证
            if( checkToken(ctx) ){
                return true;
            }
            throw new CoreException(CoreCheckException.CheckExceptionEnum.USE_TOKEN_CHECK);
        }

        protected boolean checkToken(DrawRequestContext ctx) throws CoreException{
            ActiveType type = getActiveType(ctx.getActiveTypeId());
            if( StringUtils.isNotEmpty(type.getPassword()) ){
                return type.getPassword().equals(ctx.getValueFormRequest(DrawConstantFactory.TOKEN,"").get());
            }
            return false;
        }
    }

    public ActiveType getActiveType(Integer activeTypeId){
        return ((IActiveTypeService) SpringContextHolder.getBean(ActiveTypeServiceImpl.ACTIVE_TYPE_SERVICE_IMPL)).findById(activeTypeId);
    }
}

package com.osidocker.open.micro.draw.system.validate;


import com.osidocker.open.micro.draw.system.AbstractCheckHandler;
import com.osidocker.open.micro.draw.system.resources.local.LocalResourceActivePrize;
import com.osidocker.open.micro.draw.system.transfer.DrawRequestContext;
import com.osidocker.open.micro.spring.SpringContextHolder;
import com.osidocker.open.micro.vo.CoreException;

/**
 * @Description:
 * @author: caoyj
 * @date: 2019年03月13日 17:07
 * @Copyright: © 麓山云
 */
public class ActivePrizeCheck extends AbstractCheckHandler<DrawRequestContext> {

    @Override
    protected boolean validate(DrawRequestContext ctx) throws CoreException {
        //初始化活动奖品列表数据,同时也相当于校验活动奖品是否有设置
        getActivePrizeService().getResource(ctx);
        return true;
    }

    private LocalResourceActivePrize getActivePrizeService(){
        return SpringContextHolder.getBean(LocalResourceActivePrize.PROVIDE_COUNT_LOCAL_RESOURCE);
    }
}

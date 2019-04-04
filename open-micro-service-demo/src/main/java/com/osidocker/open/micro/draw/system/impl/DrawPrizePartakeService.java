package com.osidocker.open.micro.draw.system.impl;

import com.osidocker.open.micro.draw.model.ActiveUsers;
import com.osidocker.open.micro.draw.model.ActiveWinning;
import com.osidocker.open.micro.draw.system.AbstractDrawProcessDecorate;
import com.osidocker.open.micro.draw.system.AbstractDrawStrategy;
import com.osidocker.open.micro.draw.system.CoreCheckException;
import com.osidocker.open.micro.draw.system.IFlushDataToDb;
import com.osidocker.open.micro.draw.system.enums.DrawEnums;
import com.osidocker.open.micro.draw.system.factory.DrawCheckHandlerFactory;
import com.osidocker.open.micro.draw.system.factory.DrawConstantFactory;
import com.osidocker.open.micro.draw.system.factory.DrawPrizeProcessFactory;
import com.osidocker.open.micro.draw.system.resources.local.LocalResourceActivePartakeDayCount;
import com.osidocker.open.micro.draw.system.resources.local.LocalResourceActivePrize;
import com.osidocker.open.micro.draw.system.resources.local.LocalResourceActivePrizeStatistics;
import com.osidocker.open.micro.draw.system.transfer.DrawRequestContext;
import com.osidocker.open.micro.draw.system.transfer.DrawResponseContext;
import com.osidocker.open.micro.draw.system.validate.*;
import com.osidocker.open.micro.spring.SpringContextHolder;
import com.osidocker.open.micro.vo.CoreException;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @Description:
 * @author: caoyj
 * @date: 2019年03月11日 10:57
 * @Copyright: © Caoyj
 */
@Service(DrawPrizePartakeService.DRAW_PRIZE_PARTAKE_SERVICE)
public class DrawPrizePartakeService extends AbstractDrawStrategy<DrawRequestContext, DrawResponseContext> {

    public static final String DRAW_PRIZE_PARTAKE_SERVICE = "drawPrizePartakeService";

    @Override
    protected DrawResponseContext process(DrawRequestContext ctx) throws CoreException {
        try {
            ctx.setBeanName(getBeanName());
            AbstractDrawProcessDecorate<DrawRequestContext,DrawResponseContext> drawProcess = DrawPrizeProcessFactory.getDrawProcess(ctx);
            return DrawPrizeProcessFactory.drawPrizePool.submit(drawProcess).get(3, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            logger.info(e.getMessage());
        } catch (ExecutionException e) {
            logger.info(e.getMessage());
        } catch (TimeoutException e) {
            logger.info(e.getMessage());
        }
        return null;
    }

    @Override
    protected String getBeanName() {
        return DRAW_PRIZE_PARTAKE_SERVICE;
    }

    @Override
    protected boolean insertDataToDb(DrawResponseContext rsp) {
        boolean success = true;
        Optional<ActiveUsers> usersOpt = rsp.getValueFormRequest(DrawConstantFactory.INSERT_USER,null);
        Optional<ActiveWinning> winningOpt = rsp.getValueFormRequest(DrawConstantFactory.INSERT_WINNING,null);
        // 添加一条新记录到 Active_Winning 表中
        if( !usersOpt.isPresent() ){
            throw new CoreException(CoreCheckException.CheckExceptionEnum.NOT_FIND_INSERT_ACTIVE_USERS);
        }
        if( !winningOpt.isPresent() ){
            throw new CoreException(CoreCheckException.CheckExceptionEnum.NOT_FIND_INSERT_ACTIVE_WINNING);
        }
        success = (usersService.insertOrUpdate(usersOpt.get()) && success);
        // 添加一条新记录到 Active_Users 表中
        success = (winningService.insertOrUpdate(winningOpt.get()) && success);
        return success;
    }

    @Override
    protected void initCheckList() {
        //设置检测器
        setCheckHandler(
            DrawCheckHandlerFactory.defaultCheckContainer.setCheckHandlers(
                //白名单IP检测
                new WhiteIpCheck(),
                //签名校验规则
                new SignCheck(),
                //添加用户信息限制规则
                new UserInfoCheck(),
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

    @Override
    protected List<IFlushDataToDb<DrawResponseContext>> getRegisterResourceList() {
        return Arrays.asList(
                SpringContextHolder.getBean(LocalResourceActivePrize.PROVIDE_COUNT_LOCAL_RESOURCE),
                SpringContextHolder.getBean(LocalResourceActivePrizeStatistics.ACTIVE_PARTAKE_STATISTICS_RESOURCE),
                SpringContextHolder.getBean(LocalResourceActivePartakeDayCount.ACTIVE_PARTAKE_RESOURCE)
        );
    }

    @Override
    protected boolean isThisStrategy(DrawRequestContext drawRequestContext) {
        return DrawEnums.BigWheel.equals(drawRequestContext.getDrawEnums());
    }
}

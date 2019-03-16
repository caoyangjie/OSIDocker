package com.osidocker.open.micro.draw.system.impl;

import com.osidocker.open.micro.draw.system.AbstractDrawProcessDecorate;
import com.osidocker.open.micro.draw.system.AbstractDrawStrategy;
import com.osidocker.open.micro.draw.system.enums.DrawEnums;
import com.osidocker.open.micro.draw.system.factory.DrawPrizeProcessFactory;
import com.osidocker.open.micro.draw.system.resources.local.LocalResourceActivePartakeDayCount;
import com.osidocker.open.micro.draw.system.resources.local.LocalResourceActivePrize;
import com.osidocker.open.micro.draw.system.resources.local.LocalResourceActivePrizeStatistics;
import com.osidocker.open.micro.draw.system.transfer.DrawRequestContext;
import com.osidocker.open.micro.draw.system.transfer.DrawResponseContext;
import com.osidocker.open.micro.vo.CoreException;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @Description:
 * @author: caoyj
 * @date: 2019年03月11日 10:57
 * @Copyright: © 麓山云
 */
@Service(DrawPrizePartakeService.DRAW_PRIZE_PARTAKE_SERVICE)
public class DrawPrizePartakeService extends AbstractDrawStrategy<DrawRequestContext, DrawResponseContext> {

    public static final String DRAW_PRIZE_PARTAKE_SERVICE = "drawPrizePartakeService";

    @Override
    protected DrawResponseContext process(DrawRequestContext ctx) throws CoreException {
        try {
            AbstractDrawProcessDecorate<DrawRequestContext,DrawResponseContext> drawProcess = DrawPrizeProcessFactory.getDrawProcess(ctx);
            return DrawPrizeProcessFactory.drawPrizePool.submit(drawProcess).get(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            logger.info(e.getMessage());
        } catch (ExecutionException e) {
            logger.info(e.getMessage());
        } catch (TimeoutException e) {
            logger.info(e.getMessage());
        }
        return fail();
    }

    @Override
    protected String[] getResourceName() {
        return (String[]) Arrays.asList(
                LocalResourceActivePrizeStatistics.ACTIVE_PARTAKE_STATISTICS_RESOURCE,
                LocalResourceActivePartakeDayCount.ACTIVE_PARTAKE_RESOURCE,
                LocalResourceActivePrize.PROVIDE_COUNT_LOCAL_RESOURCE
        ).toArray();
    }


    @Override
    protected boolean isThisStrategy(DrawRequestContext drawRequestContext) {
        return DrawEnums.BigWheel.equals(drawRequestContext.getDrawEnums());
    }
}

package com.osidocker.open.micro.draw;

import com.osidocker.open.micro.base.BaseJunit;
import com.osidocker.open.micro.draw.system.IDrawStrategy;
import com.osidocker.open.micro.draw.system.impl.DrawPrizePartakeService;
import com.osidocker.open.micro.draw.system.transfer.DrawRequestContext;
import com.osidocker.open.micro.draw.system.transfer.DrawResponseContext;
import com.osidocker.open.micro.utils.DateTimeKit;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.Date;
import java.util.HashMap;

/**
 * @Description:
 * @author: caoyj
 * @date: 2019年03月11日 11:34
 * @Copyright: © Caoyj
 */
public class CheckHandlerTest extends BaseJunit {
    private DrawRequestContext requestContext = new DrawRequestContext();

    @Autowired
    @Qualifier(DrawPrizePartakeService.DRAW_PRIZE_PARTAKE_SERVICE)
    IDrawStrategy<DrawRequestContext, DrawResponseContext> drawStrategy;

    @Before
    public void initArgs(){
        HashMap transData = new HashMap();
        transData.put("start", DateTimeKit.offsiteWeek(new Date(),-2).toDate());
        transData.put("end", DateTimeKit.offsiteWeek(new Date(),1).toDate());
        requestContext.setDrawEnums(1);
        requestContext.setToken("password");
        requestContext.setUseTokenFlag(true);
        requestContext.setTransData(transData);
    }

    @Test
    public void testDrawCheckEnd(){
        HashMap transData = (HashMap) requestContext.getTransData();
        transData.put("start", DateTimeKit.offsiteWeek(new Date(),-2).toDate());
        transData.put("end", DateTimeKit.offsiteWeek(new Date(),-1).toDate());
        drawStrategy.execute(requestContext);
    }

    @Test
    public void testDrawCheckStart(){
        HashMap transData = (HashMap) requestContext.getTransData();
        transData.put("start", DateTimeKit.offsiteWeek(new Date(),2).toDate());
        transData.put("end", DateTimeKit.offsiteWeek(new Date(),1).toDate());
        drawStrategy.execute(requestContext);
    }

    @Test
    public void testDrawLimitTimesDay(){
        requestContext.getTransData().put("day_over",true);
        drawStrategy.execute(requestContext);
    }

    @Test
    public void testDrawLimitTimesWeek(){
        requestContext.getTransData().put("week_over",true);
        drawStrategy.execute(requestContext);
    }

    @Test
    public void testDrawLimitTimesMouth(){
        requestContext.getTransData().put("mouth_over",true);
        drawStrategy.execute(requestContext);
    }

    @Test
    public void testDrawUseToken(){
        requestContext.setUseTokenFlag(true);
        requestContext.setToken("password1");
        drawStrategy.execute(requestContext);
    }

    @Test
    public void testDrawUseTokenFalse(){
        requestContext.setUseTokenFlag(false);
        requestContext.setToken("password1");
        drawStrategy.execute(requestContext);
    }
}

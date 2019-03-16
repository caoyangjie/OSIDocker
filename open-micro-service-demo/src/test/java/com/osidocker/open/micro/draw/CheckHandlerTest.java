package com.osidocker.open.micro.draw;

import com.osidocker.open.micro.draw.system.factory.DrawStrategyFactory;
import com.osidocker.open.micro.draw.system.transfer.DrawRequestContext;
import com.osidocker.open.micro.utils.DateTimeKit;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;
import java.util.HashMap;

/**
 * @Description:
 * @author: caoyj
 * @date: 2019年03月11日 11:34
 * @Copyright: © 麓山云
 */
public class CheckHandlerTest {
    private DrawRequestContext requestContext = new DrawRequestContext();

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
        DrawStrategyFactory.buildBigWheel().execute(requestContext);
    }

    @Test
    public void testDrawCheckStart(){
        HashMap transData = (HashMap) requestContext.getTransData();
        transData.put("start", DateTimeKit.offsiteWeek(new Date(),2).toDate());
        transData.put("end", DateTimeKit.offsiteWeek(new Date(),1).toDate());
        DrawStrategyFactory.buildBigWheel().execute(requestContext);
    }

    @Test
    public void testDrawLimitTimesDay(){
        requestContext.getTransData().put("day_over",true);
        DrawStrategyFactory.buildBigWheel().execute(requestContext);
    }

    @Test
    public void testDrawLimitTimesWeek(){
        requestContext.getTransData().put("week_over",true);
        DrawStrategyFactory.buildBigWheel().execute(requestContext);
    }

    @Test
    public void testDrawLimitTimesMouth(){
        requestContext.getTransData().put("mouth_over",true);
        DrawStrategyFactory.buildBigWheel().execute(requestContext);
    }

    @Test
    public void testDrawUseToken(){
        requestContext.setUseTokenFlag(true);
        requestContext.setToken("password1");
        DrawStrategyFactory.buildBigWheel().execute(requestContext);
    }

    @Test
    public void testDrawUseTokenFalse(){
        requestContext.setUseTokenFlag(false);
        requestContext.setToken("password1");
        DrawStrategyFactory.buildBigWheel().execute(requestContext);
    }
}

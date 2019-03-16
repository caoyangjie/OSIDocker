package com.osidocker.open.micro.draw;

import com.osidocker.open.micro.base.BaseJunit;
import com.osidocker.open.micro.draw.system.factory.DrawStrategyFactory;
import com.osidocker.open.micro.draw.system.transfer.DrawRequestContext;
import com.osidocker.open.micro.utils.DateTimeKit;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;
import java.util.HashMap;
import java.util.Optional;

/**
 * @Description:
 * @author: caoyj
 * @date: 2019年03月12日 13:38
 * @Copyright: © 麓山云
 */
public class AccessCountDrawPrizeTest extends BaseJunit {

    private DrawRequestContext requestContext = new DrawRequestContext();

    @Before
    public void initArgs(){
        HashMap transData = new HashMap();
        transData.put("start", DateTimeKit.offsiteWeek(new Date(),-2).toDate());
        transData.put("end", DateTimeKit.offsiteWeek(new Date(),1).toDate());
        transData.put("receive_type",4);
        transData.put("class",15);
        transData.put("uid",11);
        transData.put("nickname","这是昵称");
        transData.put("sign","88888888");
        requestContext.setDrawEnums(1);
        requestContext.setToken("password");
        requestContext.setUseTokenFlag(true);
        requestContext.setTransData(transData);
    }

    @Test
    public void testAccessDrawProcessTypeIsNotExist(){
        requestContext.getTransData().put("receive_type",3);
        DrawStrategyFactory.buildBigWheel().execute(requestContext);
    }

    @Test
    public void testAccessDrawProcess(){
        assert Optional.ofNullable(DrawStrategyFactory.buildBigWheel().execute(requestContext).getTransData()).isPresent();
    }

}

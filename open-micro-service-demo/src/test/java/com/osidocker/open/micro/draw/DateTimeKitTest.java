package com.osidocker.open.micro.draw;

import com.osidocker.open.micro.utils.DateTimeKit;
import org.junit.Test;

/**
 * @Description:
 * @author: caoyj
 * @date: 2019年03月14日 13:57
 * @Copyright: © 麓山云
 */
public class DateTimeKitTest {

    @Test
    public void testBeforeTodayInThisWeek(){
        String date = "20190312";
        DateTimeKit.beforeTodayInThisWeek(date);
        date = "20190308";
        DateTimeKit.beforeTodayInThisWeek(date);
    }

    @Test
    public void testBeforeTodayInThisMouth(){
        String date = "20190302";
        DateTimeKit.beforeTodayInThisMouth(date);
        date = "20190222";
        DateTimeKit.beforeTodayInThisMouth(date);
    }
}

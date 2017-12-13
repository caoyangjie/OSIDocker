/**
 * ========================================================
 * <p>
 * <p>
 * <p>
 * <p>
 * ========================================================
 */
package com.osidocker.open.micro.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

/**
 * @公司名称： 深圳原形信息技术有限公司
 * @类功能说明：
 * @类修改者： 杨浩
 * @创建日期： 创建于 2017/6/27 10:58
 * @修改说明：
 * @修改日期： 修改于 2017/6/27 10:58
 * @版本号： V1.0.0
 */
public class DataUtils {
    private static  final String NUMBER = "0010";
    private static  final String ZERO = "00";

    public static String getOrderNo(){
        String date = new SimpleDateFormat("yyyyMMdd").format(new Date());
        String seconds = new SimpleDateFormat("HHmmss").format(new Date());
        return date+NUMBER+getTwo()+ZERO+seconds+getTwo();
    }

    /**
     * 产生随机的2位数
     * @return
     */
    public static String getTwo(){
        Random rad=new Random();
        String result  = rad.nextInt(100) +"";
        if(result.length()==1){
            result = "0" + result;
        }
        return result;
    }


    /**
     * 微信超时时间
     * @param timeout
     * @return
     */
    public  static String getTimeExpire(int timeout){
        Calendar now=Calendar.getInstance();
        now.add(Calendar.MINUTE,timeout);
        SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMddHHmmss");
        String dateStr=sdf.format(now.getTimeInMillis());
        return dateStr;
    }


    /**
     * 时间戳
     * @return
     */
    public static Long getTimeStamp(){
        return System.currentTimeMillis()/1000;
    }

}

package com.osidocker.open.micro.draw.system.transfer;

import java.util.HashMap;
import java.util.Map;

/**
 * @Description:
 * @author: caoyj
 * @date: 2019年03月11日 11:01
 * @Copyright: © Caoyj
 */
public class DrawResponseContext extends AbstractContext {

    private Map<String,Object> statisticsMap = new HashMap<>(16);

    /**
     * 是否中奖
     */
    private boolean prizeFlag = false;

    private int retry = 0;

    public boolean isPrizeFlag() {
        return prizeFlag;
    }

    public void setPrizeFlag(boolean prizeFlag) {
        this.prizeFlag = prizeFlag;
    }

    private DrawRequestContext requestContext;

    public DrawRequestContext getRequestContext() {
        return requestContext;
    }

    public void setRequestContext(DrawRequestContext requestContext) {
        this.requestContext = requestContext;
        this.getTransData().putAll(requestContext.getTransData());
    }

    public void setStatisMapVal(String key,Object val){
        statisticsMap.put(key,val);
    }

    public void plusTry(){
        retry += 1;
    }

    public int getRetry() {
        return retry;
    }

    public Map<String, Object> getStatisticsMap() {
        return statisticsMap;
    }

    @Override
    public String toString() {
        return "DrawResponseContext{" +
                "prizeFlag=" + prizeFlag +
                ", requestContext=" + requestContext +
                '}';
    }
}

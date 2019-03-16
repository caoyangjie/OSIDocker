package com.osidocker.open.micro.draw.system.transfer;

/**
 * @Description:
 * @author: caoyj
 * @date: 2019年03月11日 11:01
 * @Copyright: © 麓山云
 */
public class DrawResponseContext extends AbstractContext {

    /**
     * 是否中奖
     */
    private boolean prizeFlag = false;

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
}

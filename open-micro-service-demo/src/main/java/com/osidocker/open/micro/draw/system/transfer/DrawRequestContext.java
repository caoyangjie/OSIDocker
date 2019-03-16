package com.osidocker.open.micro.draw.system.transfer;

import com.osidocker.open.micro.draw.system.enums.DrawEnums;
import com.osidocker.open.micro.security.vos.User;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @Description:
 * @author: caoyj
 * @date: 2019年03月11日 10:59
 * @Copyright: © 麓山云
 */
public class DrawRequestContext extends AbstractContext {
    /**
     * 抽奖模式： 枚举类型
     */
    private DrawEnums drawEnums;
    /**
     * 口令方式参与抽奖，口令密码值
     */
    private String token;

    /**
     * 是否使用口令参与抽奖
     */
    private boolean useTokenFlag = false;

    /**
     * 登录的shiro用户信息
     */
    private User user;

    /**
     * 请求流程中缓存的流程数据
     */
    private Map<String,Object> processCacheData = new HashMap<>();

    public Map<String, Object> getProcessCacheData() {
        return processCacheData;
    }

    public void setProcessCacheData(Map<String, Object> processCacheData) {
        this.processCacheData = processCacheData;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public boolean isUseTokenFlag() {
        return useTokenFlag;
    }

    public void setUseTokenFlag(boolean useTokenFlag) {
        this.useTokenFlag = useTokenFlag;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public DrawEnums getDrawEnums() {
        return drawEnums;
    }

    public void setDrawEnums(int type){
        this.drawEnums = DrawEnums.getInstance(type);
    }
}

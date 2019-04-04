package com.osidocker.open.micro.draw.system.transfer;

import com.osidocker.open.micro.draw.system.factory.DrawConstantFactory;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * @Description:
 * @author: caoyj
 * @date: 2019年03月15日 9:29
 * @Copyright: © Caoyj
 */
public abstract class AbstractContext implements Serializable {
    /**
     * 抽奖逻辑数据上下文对象
     */
    private Map<String,Object> transData=new HashMap<>(64);

    public Map<String, Object> getTransData() {
        return transData;
    }

    public void setTransData(Map<String, Object> transData) {
        this.transData = transData;
    }

    private String beanName;

    public String getBeanName() {
        return beanName;
    }

    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    /**
     * 根据请求字段获取请求字段的Optional包装对象
     * @param key       字段key
     * @param defaultValue  默认值
     * @param <T>       value对象
     * @return
     */
    public <T> Optional<T> getValueFormRequest(String key, T defaultValue){
        Optional<Map> mapOpt = Optional.ofNullable(getTransData());
        if( mapOpt.isPresent() ){
            return (Optional<T>) Optional.ofNullable(mapOpt.get().getOrDefault(key,defaultValue));
        }
        return Optional.ofNullable(defaultValue);
    }

    /**
     * 获取 获取奖品Id
     * @return
     */
    public Integer getPrizeId(){
        return getValueFormRequest(DrawConstantFactory.PRIZE_ID,-1).get();
    }


    /**
     * 获取 活动Id
     * @return
     */
    public Integer getActiveId(){
        return getValueFormRequest(DrawConstantFactory.ACTIVE_ID,-1).get();
    }

    /**
     * 获取活动类别Id
     * @return
     */
    public Integer getActiveTypeId(){
        return getValueFormRequest(DrawConstantFactory.ACTIVE_TYPE_ID,-1).get();
    }

    /**
     * 检查请求字段是否为null
     * @param fields
     * @return
     */
    public boolean checkNotNull(Stream<String> fields){
        return fields.allMatch(key-> getValueFormRequest(key,null)!=null);
    }
}

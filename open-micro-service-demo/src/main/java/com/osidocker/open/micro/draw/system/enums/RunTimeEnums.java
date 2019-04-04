package com.osidocker.open.micro.draw.system.enums;

/**
 * @Description:
 * @author: caoyj
 * @date: 2019年03月12日 16:48
 * @Copyright: © Caoyj
 */
public enum RunTimeEnums {
    /** 本地环境 **/
    LOCAL(1),
    /** redis环境 **/
    REDIS(2),
    /** zookeeper环境 **/
    ZOOKEEPER(3);

    Integer index;

    RunTimeEnums(Integer index){
        this.index = index;
    }

}

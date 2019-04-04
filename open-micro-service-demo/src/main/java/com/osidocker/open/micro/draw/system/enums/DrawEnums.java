package com.osidocker.open.micro.draw.system.enums;

import java.util.Arrays;

/**
 * @Description:
 * @author: caoyj
 * @date: 2019年03月11日 12:00
 * @Copyright: © Caoyj
 */
public enum  DrawEnums {
    /**
     * 大转盘枚举值
     */
    BigWheel(1,"大转盘"),
    /**
     * 九宫格枚举值
     */
    Squared(2,"九宫格"),

    Unkown(99,"异常类型");

    int type;
    String desc;

    DrawEnums(int type,String desc){
        this.type = type;
        this.desc = desc;
    }

    /**
     * 根据 类型获取对象
     * @param type 类型
     * @return
     */
    public static DrawEnums getInstance(int type){
        return Arrays.asList(DrawEnums.values()).stream().filter(de->de.type==type).findFirst().orElse(DrawEnums.Unkown);
    }
}

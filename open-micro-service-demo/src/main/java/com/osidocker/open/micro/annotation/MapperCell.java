package com.osidocker.open.micro.annotation;

import java.lang.annotation.*;

/**
 * 用于映射实体类和Excel某列名称
 *
 * @author caoyangjie
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MapperCell {

    /**
     * 在excel文件中某列数据的名称
     *
     * @return 名称
     */
    String cellName() default "";

    /**
     * 在excel中列的顺序，从小到大排
     *
     * @return 顺序
     */
    int order() default 0;

    /**
     * 转换类对象
     * @return
     */
    Class convert() default StringFunction.class;
}

package com.osidocker.open.micro.annotation;

import java.lang.annotation.*;

/**
 * @className: ApolloIngoreField
 * @description:
 * @author: caoyangjie
 * @date: 2021/10/25
 **/
@Target(value={ElementType.METHOD,ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ApolloIgnoreField {
}

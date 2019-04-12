package com.osidocker.open.micro.annotation;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface Check {
    Class<?> type();

    String method();
}

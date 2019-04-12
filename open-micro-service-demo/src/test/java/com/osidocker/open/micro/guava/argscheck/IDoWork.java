package com.osidocker.open.micro.guava.argscheck;

import com.osidocker.open.micro.annotation.Check;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author Administrator
 * @creato 2019-04-11 20:31
 */
public interface IDoWork<Request,Response> {

    @Nonnull Response execute(@Nonnull Request request);
    @Nonnull Response execute(@Nonnull Request request,@Nonnull String hello,@Nullable String allowNull,@Check(type = CheckValidate.class,method = "isTrue") Boolean notNull);
    @Nullable Response process(@Nullable Request request);
}

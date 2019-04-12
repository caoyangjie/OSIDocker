package com.osidocker.open.micro.guava.argscheck;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author Administrator
 * @creato 2019-04-11 20:34
 */
public class DoWorkService implements IDoWork {
    @Override
    public Object execute(@Nonnull Object o) {
        System.out.println(this.getClass().getSimpleName()+o);
        return o;
    }

    @Override
    public Object execute(@Nonnull Object o, @Nonnull String hello, @Nullable String allowNull, @Nonnull Boolean notNull) {
        System.out.println(this.getClass().getSimpleName()+o+hello+allowNull+notNull);
        return notNull;
    }

    @Override
    public Object process(@Nullable Object o) {
        System.out.println(this.getClass().getSimpleName()+o);
        return o;
    }
}

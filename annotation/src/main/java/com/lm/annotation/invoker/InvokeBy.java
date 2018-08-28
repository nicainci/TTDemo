package com.lm.annotation.invoker;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.SOURCE)
public @interface InvokeBy {
    Class invokerClass() default InvokeBy.class;

    String invokerName() default "";

    String methodId();

    int priority() default Integer.MAX_VALUE;
}

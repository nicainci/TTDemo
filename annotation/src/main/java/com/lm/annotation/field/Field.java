package com.lm.annotation.field;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.SOURCE)
public @interface Field {
    String value() default "";

    Class asClass() default Object.class;

    boolean doAdditionalFetch() default false;
}

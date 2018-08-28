package com.lm.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Target 作用：
 * 注解的使用范围
 * @Target 取值：
 * ElementType.TYPE             用于注解接口、类、枚举、注解
 * ElementType.CONSTRUCTOR      用于注解构造函数
 * ElementType.METHOD           用于注解方法
 * ElementType.PARAMETER        用于注解方法参数
 * ElementType.FIELD            用于注解字段
 * ElementType.LOCAL_VARIABLE   用于注解局部变量
 * ElementType.ANNOTATION_TYPE  用于注解注解
 * ElementType.PACKAGE          用于注解包名
 */
@Target(ElementType.TYPE)
/**
 * @Retention 作用：
 * 注解保留策略
 * @Retention 取值：
 * RetentionPolicy.SOURCE       仅存在于源码中，在class字节码文件中不包含
 * RetentionPolicy.CLASS        默认的保留策略，注解会在class字节码文件中存在，但运行时无法获得
 * RetentionPolicy.RUNTIME      注解会在class字节码文件中存在，在运行时可以通过反射获取到
 */
@Retention(RetentionPolicy.SOURCE)
public @interface StandFor {
    Class forClass() default StandFor.class;

    String forName() default "";
}

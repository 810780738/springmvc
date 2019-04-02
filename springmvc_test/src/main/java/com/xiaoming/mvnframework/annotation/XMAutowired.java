package com.xiaoming.mvnframework.annotation;

import java.lang.annotation.*;

/**
 * @Auther: Administrator
 * @Date: 2018/12/27 19:26
 * @Description:
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface XMAutowired {
    String value() default "";
}

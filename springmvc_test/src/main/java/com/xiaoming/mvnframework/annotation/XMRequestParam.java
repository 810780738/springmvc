package com.xiaoming.mvnframework.annotation;

import java.lang.annotation.*;

/**
 * @Auther: Administrator
 * @Date: 2018/12/27 19:28
 * @Description:
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface XMRequestParam {
    String value() default "";
}

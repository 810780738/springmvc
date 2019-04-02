package com.xiaoming.mvnframework.annotation;

import java.lang.annotation.*;

/**
 * @Auther: zsm
 * @Date: 2018/12/27 19:24
 * @Description: service注解
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface XMService {
    String value() default "";
}

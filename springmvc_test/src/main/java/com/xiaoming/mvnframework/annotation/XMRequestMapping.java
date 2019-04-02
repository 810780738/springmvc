package com.xiaoming.mvnframework.annotation;/**
 * @Auther: Administrator
 * @Date: 2018/12/27 19:22
 * @Description:
 */

import java.lang.annotation.*;

/**
 * @Author: zhusm@bsoft.com.cn
 *
 * @Description: mapping
 *
 * @Create: 2018-12-27 19:22
 **/
@Target({ElementType.TYPE,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface XMRequestMapping {
    String value() default "";
}

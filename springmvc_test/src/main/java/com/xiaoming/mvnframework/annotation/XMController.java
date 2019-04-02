package com.xiaoming.mvnframework.annotation;/**
 * @Auther: Administrator
 * @Date: 2018/12/27 19:17
 * @Description:
 */

/**
 * @Author: zhusm@bsoft.com.cn
 *
 * @Description: Controllet控制中心注解
 *
 * @Create: 2018-12-27 19:17
 **/

import java.lang.annotation.*;

@Target({ElementType.TYPE}) //标识作用在class，接口或枚举上
@Retention(RetentionPolicy.RUNTIME)//运行时生效
@Documented //作用在javadoc
public @interface XMController {
    String value() default "";
}

package com.xiaoming.init.service.impl;/**
 * @Auther: Administrator
 * @Date: 2018/12/27 19:31
 * @Description:
 */

import com.xiaoming.init.service.IService;
import com.xiaoming.mvnframework.annotation.XMService;

/**
 * @Author: zhusm@bsoft.com.cn
 *
 * @Description: test
 *
 * @Create: 2018-12-27 19:31
 **/
@XMService
public class IServiceImpl implements IService {
    public String get(String name) {
        return "xiaozhu";
    }
}

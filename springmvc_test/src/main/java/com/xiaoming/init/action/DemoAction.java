package com.xiaoming.init.action;/**
 * @Auther: Administrator
 * @Date: 2018/12/27 19:30
 * @Description:
 */

import com.xiaoming.init.service.IService;
import com.xiaoming.mvnframework.annotation.XMAutowired;
import com.xiaoming.mvnframework.annotation.XMController;
import com.xiaoming.mvnframework.annotation.XMRequestMapping;
import com.xiaoming.mvnframework.annotation.XMRequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Author: zhusm@bsoft.com.cn
 *
 * @Description: 注解生效
 *
 * @Create: 2018-12-27 19:30
 **/
@XMController
@XMRequestMapping("/demo")
public class DemoAction {

    @XMAutowired
    private IService service;

    @XMRequestMapping("/query.json")
    public void query(HttpServletRequest request, HttpServletResponse response, @XMRequestParam("name") String name){
        String result = service.get(name);
        try {
            response.getWriter().write(result);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

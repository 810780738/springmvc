package com.xiaoming.mvnframework.servlet;

import com.xiaoming.mvnframework.annotation.XMAutowired;
import com.xiaoming.mvnframework.annotation.XMController;
import com.xiaoming.mvnframework.annotation.XMRequestMapping;
import com.xiaoming.mvnframework.annotation.XMService;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.*;

/**
 * @Author: zhusm@bsoft.com.cn
 *
 * @Description: servlet
 *
 * @Create: 2018-12-27 19:10
 **/
public class XMServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    //与.webxml中的init-parm中一致
    private static final String LOCATION = "contextConfigLocation";

    //保存所有的配置信息
    private Properties p = new Properties();

    //保存所有被扫描到的类名
    private List<String> classNames = new ArrayList<String>();

    //ioc容器 初始化Bean
    private Map<String,Object> ioc = new HashMap<String, Object>();

    //保存所有ur与方法的映射关系
    private Map<String,Method> handlerMapping = new HashMap<String, Method>();

    public XMServlet() {
        super();
    }


    @Override
    public void init(ServletConfig config){
        //加载配置文件
        doLocalConfig(config.getInitParameter(LOCATION));

        //扫描相关类
        doScanner(p.getProperty("scanPackage"));

        //初始化所有相关类并保存到IOC容器中
        doInstance();

        //依赖注入
        doAutowired();

        //构造HandlerMapping
        initHandlerMapping();

        System.out.println("xiaozhu springmvc run....");
    }

    private void initHandlerMapping() {
        if (ioc.isEmpty()) return;
        for (Map.Entry<String,Object> entry : ioc.entrySet()) {
            Class<?> clazz = entry.getValue().getClass();
            if (!clazz.isAnnotationPresent(XMController.class)) continue;
            String baseUrl = "";
            //获取controller的url配置
            if (clazz.isAnnotationPresent(XMRequestMapping.class)){
                XMRequestMapping requestMapping = clazz.getAnnotation(XMRequestMapping.class);
                baseUrl = requestMapping.value();
            }

            //获取method上的url配置
            Method[] methods = clazz.getMethods();
            for (Method method : methods){
                if (!method.isAnnotationPresent(XMRequestMapping.class)) continue;
                XMRequestMapping requestMapping = method.getAnnotation(XMRequestMapping.class);
                String url = ("/" + baseUrl + "/" + requestMapping.value()).replaceAll("/+", "/");
                handlerMapping.put(url,method);
                System.out.println("mapped ---->"+url);
            }
        }
    }

    private void doAutowired() {
        if (ioc.isEmpty()) return;
        for (Map.Entry<String,Object> entry : ioc.entrySet()) {
            Field[] declaredFields = entry.getValue().getClass().getDeclaredFields();
            for (Field field : declaredFields) {
                if (!field.isAnnotationPresent(XMAutowired.class)) continue;
                XMAutowired autowired = field.getAnnotation(XMAutowired.class);
                String beanName = autowired.value().trim();
                if ("".equals(beanName)){
                    beanName = field.getType().getName();
                }
                //设置私有属性访问权限
                field.setAccessible(true);
                try {
                    field.set(entry.getValue(),ioc.get(beanName));
                    System.out.println("Autowired ----->"+beanName);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                    continue;
                }
            }
        }
    }

    /**
     * @Author: zhusm@bsoft.com.cn
     * @Description: ioc容器初始化
     * @CreateTime: 20:13 2018/12/27
     * @Params: []
     * @return: void
     **/
    private void doInstance() {
        if (classNames.size() == 0) return;
        try {
            for (String className : classNames ) {
                Class<?> clazz = Class.forName(className);//获取className的接口或类
                if (clazz.isAnnotationPresent(XMController.class)){
                    //类名换成小写
                    String beanName = loweFirstCase(clazz.getSimpleName());
                    //装载并创建初始化
                    System.out.println(beanName+"----->init.....");
                    ioc.put(beanName,clazz.newInstance());
                }else if (clazz.isAnnotationPresent(XMService.class)){
                    XMService service = clazz.getAnnotation(XMService.class);
                    String beanName = service.value();
                    if (!"".equals(beanName.trim())){
                        //用户自定义了service名称则使用自定义名称
                        ioc.put(beanName,clazz.newInstance());
                        continue;
                    }
                    //没有自定义名称
                    Class<?>[] interfaces = clazz.getInterfaces();
                    for (Class<?> i : interfaces) {
                        ioc.put(i.getName(),clazz.newInstance());
                    }
                }else{
                    continue;
                }


            }

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
    }
    
    /***
     * @Author: zhusm@bsoft.com.cn
     * @Description: 扫描所有的类
     * @CreateTime: 20:06 2018/12/27
     * @Params: [packageName]
     * @return: void
     **/
    private void doScanner(String packageName) {
        URL url = this.getClass().getClassLoader().getResource("/" + packageName.replaceAll("\\.", "/"));
        File dir = new File(url.getFile());
        for (File file : dir.listFiles()
             ) {
            if (file.isDirectory()){
                doScanner(packageName+"."+file.getName());
            }else {
                System.out.println("Scanner ---->"+packageName+"."+file.getName().replace(".class","").trim());
                classNames.add(packageName+"."+file.getName().replace(".class","").trim());
            }
        }
    }

    private void doLocalConfig(String localtion) {
        InputStream is = null;
        try {
            is = this.getClass().getClassLoader().getResourceAsStream(localtion);
            p.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(null != is){
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * @Author: zhusm@bsoft.com.cn
     * @Description: IOC容器中的类名都是小写
     * @CreateTime: 19:59 2018/12/27
     * @Params: [str]
     * @return: java.lang.String
     **/
    private String loweFirstCase(String str){
        char[] chars = str.toCharArray();
        chars[0] += 32;
        return String.valueOf(chars);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//        super.doPost(req, resp);
        try {
            doDispatch(req,resp);
        } catch (Exception e) {
            resp.getWriter().write("500 Exception");
        }
    }

    private void doDispatch(HttpServletRequest req, HttpServletResponse resp) throws IOException, InvocationTargetException, IllegalAccessException {
        if (this.handlerMapping.isEmpty()) return;
        String uri = req.getRequestURI();
        String contextPath = req.getContextPath();
        uri = uri.replace(contextPath,"").replaceAll("/+","/");
        if (!this.handlerMapping.containsKey(uri)){
            resp.getWriter().write("404 not found!!!");
            return;
        }

        Map params = req.getParameterMap();
        Method method = this.handlerMapping.get(uri);
        Class<?>[] parameterTypes = method.getParameterTypes();
        Map<String,String[]> parameterMap =  req.getParameterMap();
        //保存参数值
        Object[] paramValues = new Object[parameterTypes.length];
        for(int i = 0;i < paramValues.length;i++){
            Class<?> parameterType = parameterTypes[i];
            if (parameterType == HttpServletRequest.class){
                paramValues[i] = req;
                continue;
            }else if (parameterType == HttpServletResponse.class){
                paramValues[i] = resp;
                continue;
            }else if (parameterType == String.class){
                for (Map.Entry<String,String[]> param : parameterMap.entrySet()) {
                    String value = Arrays.toString(param.getValue())
                            .replaceAll("\\[|\\]","")
                            .replaceAll("\\s",",");
                    paramValues[i] = value;

                }
            }
        }

        String beanName = loweFirstCase(method.getDeclaringClass().getSimpleName());
        method.invoke(this.ioc.get(beanName),paramValues);

    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doPost(req,resp);
    }
}

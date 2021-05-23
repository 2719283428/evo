package com.evo.crm.web.listener;


import com.evo.crm.workbench.service.DicService;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class SysInitListener implements ServletContextListener {

    //启动服务器时自动调用此方法
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        DicService dicService = WebApplicationContextUtils.getWebApplicationContext(sce.getServletContext()).getBean(DicService.class);
        dicService.setDictionaryRedis();


        ServletContext application = sce.getServletContext();
        Map<String,String> pMap = new HashMap();
        //解析properties文件
        ResourceBundle bundle = ResourceBundle.getBundle("conf/Stage2Possibility");
        Enumeration<String> e = bundle.getKeys();
        while (e.hasMoreElements()){
            //阶段
            String key = e.nextElement();
            //可能性
            String value = bundle.getString(key);
            pMap.put(key,value);
        }
        application.setAttribute("pMap",pMap);

    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {

    }
}

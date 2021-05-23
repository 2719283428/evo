package com.evo.crm.workbench.web.controller;

import com.evo.crm.settings.service.UserService;
import com.evo.crm.utils.MD5Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Controller
public class UserController {
    @Autowired
    private UserService userService;

    @RequestMapping(value = "/settings/user/login.do")
    public @ResponseBody Map<String,Object> login(String loginAct, String loginPwd, HttpServletRequest request) {
        //将密码转换为MD5的密文格式
        loginPwd = MD5Util.getMD5(loginPwd);
        //获取用户IP地址
        String ip = request.getRemoteAddr();


        //调用login方法得到user对象和登录信息
        Map<String,Object> logMap = userService.login(loginAct, loginPwd, ip);
        //判断登录标记
        if(!(Boolean)logMap.get("success")){
            return logMap;
        }
        //将User对象放入到session
        request.getSession().setAttribute("user", logMap.get("user"));
        //返回登录信息
        return logMap;
    }

}

package com.evo.crm.web.Interceptor;

import com.evo.crm.settings.domain.User;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class LoginInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        User user = (User) request.getSession().getAttribute("user");
        if(null != user){
            return false;
        }
        //使用重定向转发
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return true;
    }

}

package com.evo.crm.web.filter;


import com.evo.crm.settings.domain.User;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class LoginFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
        HttpServletResponse response = (HttpServletResponse) resp;
        HttpServletRequest request = (HttpServletRequest) req;
        User user = (User) request.getSession().getAttribute("user");


        //放行登录请求
        String path = request.getServletPath();
        if("/login.jsp".equals(path) || "settings/user/login.do".equals(path)){
            chain.doFilter(req,resp);
        }else{
            //判断是否登录过，未登录拦截
            if(null != user){
                chain.doFilter(req,resp);
            }else{
                response.sendRedirect(request.getContextPath()+"/login.jsp");
            }

        }
    }

    @Override
    public void destroy() {

    }
}

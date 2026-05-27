package com.mybatisplus.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mybatisplus.common.Result;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Component
public class LoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 获取请求路径
        String uri = request.getRequestURI();

        // 放行登录、注册、商品列表、商品详情等公开接口
        if (uri.contains("/login") || uri.contains("/register") || 
            uri.contains("/goods/list") || uri.contains("/goods/get") ||
            uri.contains("/banner/list") || uri.contains("/category/list") ||
            uri.contains("/seller/get")) {
            return true;
        }

        // 获取 session
        HttpSession session = request.getSession();
        Object user = session.getAttribute("user");
        Object admin = session.getAttribute("admin");
        Object seller = session.getAttribute("seller");

        // 如果都没有登录，返回错误
        if (user == null && admin == null && seller == null) {
            response.setContentType("application/json;charset=UTF-8");
            Result<Object> result = Result.error("请先登录");
            ObjectMapper objectMapper = new ObjectMapper();
            response.getWriter().write(objectMapper.writeValueAsString(result));
            return false;
        }

        return true;
    }
}

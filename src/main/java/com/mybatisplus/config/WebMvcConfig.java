package com.mybatisplus.config;

import com.mybatisplus.interceptor.LoginInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private LoginInterceptor loginInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/user/login", "/user/register",
                                     "/goods/list", "/goods/get/**",
                                     "/banner/list", "/category/list",
                                     "/seller/login", "/seller/register",
                                     "/seller/get/**", "/admin/login");
    }
}

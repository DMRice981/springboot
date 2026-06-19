package com.mybatisplus.config;

import com.mybatisplus.interceptor.LoginInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private LoginInterceptor loginInterceptor;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // context-path 为 /api，拦截器路径匹配不含 context-path
        registry.addInterceptor(loginInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                    // ===== 登录/注册接口（公开）=====
                    "/user/login", "/user/register",
                    "/seller/login", "/seller/register",
                    "/admin/login",

                    // ===== 商品浏览（公开）=====
                    "/goods/list",
                    "/goods/list/paged",
                    "/goods/get/**",
                    "/goods/*",
                    "/goods/search",

                    // ===== 分类（公开）=====
                    "/category/list",
                    "/category/get/**",

                    // ===== 轮播图（公开）=====
                    "/banner/list",
                    "/banner/get/**",

                    // ===== 商家信息（公开）=====
                    "/seller/get/**",
                    "/seller/list",
                    "/seller/list/paged",

                    // ===== 商品图片（公开）=====
                    "/goodsImg/list/**",

                    // ===== 商品评论（公开）=====
                    "/comment/list",
                    "/comment/list/**",
                    "/comment/get/**",
                    "/comment/add",

                    // ===== 聊天功能（公开，连接后通过参数验证身份）=====
                    "/chat/**"
                );
    }
}

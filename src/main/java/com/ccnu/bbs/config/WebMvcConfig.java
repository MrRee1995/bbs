package com.ccnu.bbs.config;

import com.ccnu.bbs.Interceptor.AuthorizationInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Component
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    AuthorizationInterceptor authorizationInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        InterceptorRegistration loginRegistry = registry.addInterceptor(authorizationInterceptor);
        // 拦截路径
        loginRegistry.addPathPatterns("/**");
        // 排除路径
        loginRegistry.excludePathPatterns("/wechat/**");
        loginRegistry.excludePathPatterns("/article/list");
        loginRegistry.excludePathPatterns("/article/search");
        loginRegistry.excludePathPatterns("/topic/**");
    }
}

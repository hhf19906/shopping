package com.hhf.shopping.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

//自定义拦截器
@Configuration
public class WebMvcConfiguration extends WebMvcConfigurerAdapter {
    @Autowired
    AuthInterceptor authInterceptor;


    public void addInterceptors(InterceptorRegistry registry){
        registry.addInterceptor(authInterceptor).addPathPatterns("/**");
        super.addInterceptors(registry);
    }
}

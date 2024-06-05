package com.nlpcaptcha.captcha;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://193.196.66.76")// TODO dont allow all origins
                .allowedMethods("*")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}
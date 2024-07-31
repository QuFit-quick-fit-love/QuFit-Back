package com.cupid.qufit.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // 모든 경로에 대해 이 CORS 설정을 적용
                .allowedOriginPatterns("*")
//                .allowedOrigins(
//                        "http://localhost:3000",
//                        "http://i11a209.p.ssafy.io:8080",
//                        "https://i11a209.p.ssafy.io:8080",
//                        "http://i11a209.p.ssafy.io",
//                        "https://i11a209.p.ssafy.io"
//                )
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }

    // ! Swagger를 위한 설정
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/swagger-ui/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/springdoc-openapi-ui/")
                .resourceChain(false);
    }
}

package com.cupid.qufit.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /*
    * * 각 URL 패턴에 대한 보안 필터 설정
    * */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
//                                new AntPathRequestMatcher("/api/auth/login")
                                new AntPathRequestMatcher("/**") // (임시 테스트용) 모두 접근가능
                        ).permitAll())
        ;

        return http.build();
    }
}
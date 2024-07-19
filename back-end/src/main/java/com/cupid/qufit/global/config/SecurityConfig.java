package com.cupid.qufit.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
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
                                "/**" // (임시 테스트용) 모두 접근가능
                        ).permitAll())
                //외부 post 요청을 받아야 하는 csrf // disable
                .csrf(AbstractHttpConfigurer::disable)
                //JWT등을 사용할떄 SpringSecurity가 세션을 생성하지도않고, 기본것을 사용하지도 않게끔 STATELESS로 설정
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        ;

        return http.build();
    }
}
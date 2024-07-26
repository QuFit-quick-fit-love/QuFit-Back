package com.cupid.qufit.global.config;

import com.cupid.qufit.global.redis.service.RedisRefreshTokenService;
import com.cupid.qufit.global.security.filter.JWTCheckExceptionFilter;
import com.cupid.qufit.global.security.filter.JWTCheckFilter;
import com.cupid.qufit.global.security.handler.CustomAccessDeniedHandler;
import com.cupid.qufit.global.security.util.JWTUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {

    private final JWTUtil jwtUtil;
    private final RedisRefreshTokenService redisRefreshTokenService;

    /*
     * * 각 URL 패턴에 대한 보안 필터 설정
     * */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth -> auth
                    .requestMatchers(
//                                new AntPathRequestMatcher("/api/auth/login")
                            "/**" // (임시 테스트용) 모두 접근가능
                    ).permitAll())
            // csrf 사용 x
            .csrf(AbstractHttpConfigurer::disable)
            // 세션 사용 x
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // jwt 토큰 확인 필터
        http.addFilterBefore(new JWTCheckFilter(jwtUtil, redisRefreshTokenService),
                             UsernamePasswordAuthenticationFilter.class)
            // JWTCheckFilter를 실행하면서 발생하는 error를 handle함
            .addFilterBefore(new JWTCheckExceptionFilter(), JWTCheckFilter.class)
            .exceptionHandling(config -> {
                config.accessDeniedHandler(new CustomAccessDeniedHandler());
            });

        return http.build();
    }
}
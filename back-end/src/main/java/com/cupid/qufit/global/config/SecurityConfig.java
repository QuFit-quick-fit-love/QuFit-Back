package com.cupid.qufit.global.config;

import com.cupid.qufit.global.redis.service.RedisRefreshTokenService;
import com.cupid.qufit.global.security.filter.JWTCheckExceptionFilter;
import com.cupid.qufit.global.security.filter.JWTCheckFilter;
import com.cupid.qufit.global.security.handler.CustomAccessDeniedHandler;
import com.cupid.qufit.global.security.handler.CustomLoginFailureHandler;
import com.cupid.qufit.global.security.handler.CustomLoginSuccessHandler;
import com.cupid.qufit.global.security.handler.CustomLogoutSuccessHandler;
import com.cupid.qufit.global.security.service.CustomLogoutService;
import com.cupid.qufit.global.security.util.JWTUtil;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {

    private final JWTUtil jwtUtil;

    /*
     * * 각 URL 패턴에 대한 보안 필터 설정
     * */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable);
                // 세션 사용 x
//                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
//        httpSecurity.cors(cors -> cors.configurationSource(corsConfigurationSource()));
        // jwt 토큰 확인 필터
//        http.addFilterBefore(new JWTCheckFilter(jwtUtil),
//                             UsernamePasswordAuthenticationFilter.class)
//            // JWTCheckFilter를 실행하면서 발생하는 error를 handle함
//            .addFilterBefore(new JWTCheckExceptionFilter(), JWTCheckFilter.class)
//            .exceptionHandling(config -> {
//                config.accessDeniedHandler(new CustomAccessDeniedHandler());
//            });

        // 로그아웃
        http.logout(
                logout -> logout
                        .logoutRequestMatcher(new AntPathRequestMatcher("/qufit/member/logout"))
                        .addLogoutHandler(new CustomLogoutService(jwtUtil))
                        .logoutSuccessHandler(new CustomLogoutSuccessHandler()));

        // 관리자 로그인
        http.formLogin(login -> login
                .usernameParameter("userId")
                .passwordParameter("password")
                .loginProcessingUrl("/qufit/admin/login")
                .successHandler(new CustomLoginSuccessHandler(jwtUtil))
                .failureHandler(new CustomLoginFailureHandler())
        );

        // 권한 설정
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/qufit/admin/**").hasRole("ADMIN") // admin으로 시작하는 url은 admin 권한 보유자만 접근 가능
                .anyRequest().permitAll());

        return http.build();
    }

    // 비밀번호 암호화
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // AuthenticationManager 빈 등록
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.setAllowedOrigins(Arrays.asList("http://localhost:3000")); // 프론트엔드 도메인
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(Arrays.asList("*"));
        // 필요 시 클라이언트에서 접근해야 하는 헤더 추가 (예: Set-Cookie)
        config.setExposedHeaders(Arrays.asList("Set-Cookie", "Location", "Content-Disposition"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}

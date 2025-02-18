package com.cupid.qufit.global.security.handler;

import com.cupid.qufit.domain.member.dto.MemberDetails;
import com.cupid.qufit.global.common.response.CommonResultResponse;
//import com.cupid.qufit.global.redis.service.RedisRefreshTokenService;
import com.cupid.qufit.global.security.util.JWTUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

/*
* * form형태로 요청되는 관리자 로그인 성공 처리
*
* @return : accessToken header 주입
* */
@Log4j2
@RequiredArgsConstructor
public class CustomLoginSuccessHandler implements AuthenticationSuccessHandler {

    private final JWTUtil jwtUtil;
//    private final RedisRefreshTokenService redisRefreshTokenService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
//        log.info("------------admin login success-----------");
//        MemberDetails adminDetails = (MemberDetails) authentication.getPrincipal();
//        String accessToken = jwtUtil.generateToken(adminDetails.getClaims(), "access");
//        String refreshToken = jwtUtil.generateToken(adminDetails.getClaims(), "refresh");
////        redisRefreshTokenService.saveRedisData(adminDetails.getId(), refreshToken, accessToken); // refreshToken redis에 저장
//
//        log.info("adminDetails" + adminDetails.getClaims());
//
//        CommonResultResponse adminLoginResult = CommonResultResponse.builder()
//                                                                .isSuccess(true)
//                                                                .message("관리자 로그인되었습니다.")
//                                                                .build();
//        // JSON으로 변환
//        String jsonResponse = new ObjectMapper().writeValueAsString(adminLoginResult);
//
//        // 응답에 Error Response 저장
//        response.setStatus(200);
//        response.setContentType("application/json;charset=UTF-8");
//        response.getWriter().write(jsonResponse);
//        response.addHeader("Authorization", "Bearer " + accessToken);
    }
}

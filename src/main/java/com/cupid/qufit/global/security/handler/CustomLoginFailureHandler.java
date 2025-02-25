package com.cupid.qufit.global.security.handler;

import com.cupid.qufit.global.common.response.CommonResultResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

/*
 * * form형태로 요청되는 관리자 로그인 실패 처리
 *
 * */
@Log4j2
@RequiredArgsConstructor
public class CustomLoginFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {
        log.info("------------admin login failure-----------");
        CommonResultResponse adminLoginResult = CommonResultResponse.builder()
                                                                    .isSuccess(false)
                                                                    .message("관리자 로그인을 실패했습니다.")
                                                                    .build();
        // JSON으로 변환
        String jsonResponse = new ObjectMapper().writeValueAsString(adminLoginResult);

        // 응답에 Error Response 저장
        response.setStatus(200);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(jsonResponse);
    }
}

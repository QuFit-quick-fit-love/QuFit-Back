package com.cupid.qufit.global.security.handler;

import com.cupid.qufit.global.common.response.CommonResultResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

@Log4j2
public class CustomLogoutSuccessHandler implements LogoutSuccessHandler {

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {
        log.info("----logout success---");

        CommonResultResponse logoutResult = CommonResultResponse.builder()
                                                                .isSuccess(true)
                                                                .message("로그아웃되었습니다.")
                                                                .build();
        // JSON으로 변환
        String jsonResponse = new ObjectMapper().writeValueAsString(logoutResult);

        // 응답에 Error Response 저장
        response.setStatus(200);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(jsonResponse);
    }
}

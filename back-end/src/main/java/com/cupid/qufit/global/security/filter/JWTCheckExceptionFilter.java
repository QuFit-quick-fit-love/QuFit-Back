package com.cupid.qufit.global.security.filter;

import static com.cupid.qufit.global.security.util.SecurityErrorResponseUtil.setSecurityErrorResponse;

import com.cupid.qufit.global.exception.ErrorCode;
import com.cupid.qufit.global.exception.ErrorResponse;
import com.cupid.qufit.global.exception.exceptionType.CustomJWTException;
import com.cupid.qufit.global.security.util.SecurityErrorResponseUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.web.filter.OncePerRequestFilter;

/*
* * JWT CheckFilter에서 발생한 예외 사항을 처리함
* */
public class JWTCheckExceptionFilter extends OncePerRequestFilter {

    /*
     * JWT CheckFilter를 실행하고 JWT CheckFilter에서 발생한 예외 사항을 처리함
     * */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (CustomJWTException e) {
            SecurityErrorResponseUtil.setSecurityErrorResponse(e, response, request);
        }

    }
}

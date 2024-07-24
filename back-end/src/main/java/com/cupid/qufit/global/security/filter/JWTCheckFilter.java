package com.cupid.qufit.global.security.filter;

import com.cupid.qufit.domain.member.dto.MemberDetails;
import com.cupid.qufit.entity.MemberRole;
import com.cupid.qufit.global.exception.ErrorCode;
import com.cupid.qufit.global.exception.exceptionType.CustomJWTException;
import com.cupid.qufit.global.security.util.JWTUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InvalidClassException;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

/*
 * * 모든 요청의 jwt를 검증하는 필터
 * */
@Log4j2
@RequiredArgsConstructor
public class JWTCheckFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {

        String path = request.getRequestURI();
        if (path.startsWith("/auth")) {
            return true; // 로그인, 회원가입 관련 api는 check하지 않음
        }
        return false;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        log.info("-------jwt check filter-------");

        String accessToken = jwtUtil.getTokenFromHeader(request);

        // AccessToken 만료여부 확인
        if (!jwtUtil.checkTokenExpired(accessToken)) {
            // AccessToken 유효하면 Authentication 설정
            setAuthentication(accessToken);
        } else {
            // TODO : AccessToken 만료되었으면  refreshToken 확인
        }
        filterChain.doFilter(request, response);
    }

    private void setAuthentication(String accessToken) {
        try {
            log.info("--------setAuthentication-----");
            Map<String, Object> claims = jwtUtil.validateToken(accessToken);
            log.info("claims : " + claims);

            Long id = ((Number) claims.get("id")).longValue();
            String email = (String) claims.get("email");
            MemberRole role = claims.get("role").equals("USER") ? MemberRole.USER : MemberRole.ADMIN;

            MemberDetails memberDetails = new MemberDetails(id, email, role);
            log.info("memberROLE : " + memberDetails.getAuthorities());

            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    memberDetails, "", memberDetails.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        } catch (InvalidClassException e) {
            throw new RuntimeException(e);
        }
    }
}

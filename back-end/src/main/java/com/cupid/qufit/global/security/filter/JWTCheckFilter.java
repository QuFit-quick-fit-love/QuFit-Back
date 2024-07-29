package com.cupid.qufit.global.security.filter;

import com.cupid.qufit.domain.member.dto.MemberDetails;
import com.cupid.qufit.entity.MemberRole;
import com.cupid.qufit.global.exception.ErrorCode;
import com.cupid.qufit.global.exception.exceptionType.CustomJWTException;
import com.cupid.qufit.global.redis.service.RedisRefreshTokenService;
import com.cupid.qufit.global.security.util.JWTUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
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
    private final RedisRefreshTokenService redisRefreshTokenService;

    /*
    * * 토큰 검증없이 접근 가능한 api를 설정함
    * */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {

        String path = request.getRequestURI();
        if (path.startsWith("/qufit/auth")) {
            return true; // 로그인, 회원가입 관련 api는 check하지 않음
        }
        return false;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        log.info("-------jwt check filter-------");

        String accessToken = jwtUtil.getTokenFromHeader(request);

        // AccessToken 유효성 검사
        if (!jwtUtil.checkTokenExpired(accessToken)) {
            // AccessToken 유효
            log.info("[accessToken is valid]");
            setAuthentication(accessToken);
        } else {
            log.info("[accessToken is invalid]");
            // AccessToken 만료 -> RefreshToken 확인
            String refreshToken = redisRefreshTokenService.getRedisDataByAccessToken(accessToken);

            if (refreshToken != null && !jwtUtil.checkTokenExpired(refreshToken)) {
                // RefreshToken 유효
                handleExpiredAccessToken(response, refreshToken);
            } else {
                // RefreshToken 만료
                log.error("[refresh token is expired]");
                throw new CustomJWTException(ErrorCode.EXPIRED_TOKEN);
            }
        }
        filterChain.doFilter(request, response);
    }

    /*
    * * Access Token이 만료되고, RefreshToken은 유효할 경우 실행될 메소드
    *
    * - Access Token이 새로 발급되고 response header에 저장됨
    * - Refresh Token의 유효시간이 1시간 남았다면 다시 Refresh Token을 발급해서 Redis에 저장함
    * */
    private void handleExpiredAccessToken(HttpServletResponse response, String refreshToken) {
        // refreshToken 유효 -> 새로운 accessToken 발급
        Map<String, Object> claims = jwtUtil.validateToken(refreshToken);
        Long memberId = ((Number) claims.get("id")).longValue();

        String newAccessToken = jwtUtil.generateToken(claims, "access");
        log.info("[new access token generate] ");
        setAuthentication(newAccessToken);
        jwtUtil.setTokenToHeader(response, newAccessToken);

        // refreshToken 유효시간 1시간 남았다면 새로운 refreshToken 발급
        Integer refreshTokenExp = (Integer) claims.get("exp");

        if (jwtUtil.checkTokenExpiringInAnHour(refreshTokenExp)) {
            String newRefreshToken = jwtUtil.generateToken(claims, "refresh");
            redisRefreshTokenService.saveRedisData(memberId, newRefreshToken, newAccessToken);
            log.info("[new refresh token generate] ");
        } else {
            redisRefreshTokenService.saveRedisData(memberId, refreshToken, newAccessToken);
        }
    }

    /*
    * * Security Context Holder 안에 Authentication을 저장함
    * */
    private void setAuthentication(String accessToken) {
        log.info("--------setAuthentication-----");
        Map<String, Object> claims = jwtUtil.validateToken(accessToken);
        log.info("claims : " + claims);

        Long id = ((Number) claims.get("id")).longValue();
        String email = (String) claims.get("email");
        MemberRole role = claims.get("role").equals("USER") ? MemberRole.USER : MemberRole.ADMIN;

        MemberDetails memberDetails = new MemberDetails(id, email, role);

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                memberDetails, "", memberDetails.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    }
}

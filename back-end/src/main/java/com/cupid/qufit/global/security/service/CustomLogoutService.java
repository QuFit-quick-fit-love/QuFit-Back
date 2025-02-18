package com.cupid.qufit.global.security.service;

import com.cupid.qufit.global.exception.ErrorCode;
import com.cupid.qufit.global.exception.exceptionType.CustomJWTException;
import com.cupid.qufit.global.security.util.JWTUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;

import java.util.Date;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

/*
 * * 로그아웃 처리
 *
 * - Redis에 저장된 refreshToken을 삭제함
 * - accessToken의 유효시간이 남아있다면, 남은 유효시간 동안 Redis에 "logout"으로 저장해 Black List로 등록해서 관리함
 * */
@Service
@Transactional
@RequiredArgsConstructor
@Log4j2
public class CustomLogoutService implements LogoutHandler {

    private final JWTUtil jwtUtil;
//    private final RedisRefreshTokenService redisRefreshTokenService;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        String accessToken = jwtUtil.getTokenFromHeader(request);

        // accessToken 검사
        Map<String, Object> claims = jwtUtil.validateToken(accessToken);
//        String refreshToken = redisRefreshTokenService.getRedisDataByAccessToken(accessToken);

//        if (jwtUtil.checkTokenExpired(accessToken) || jwtUtil.checkTokenExpired(refreshToken)) {
        if (jwtUtil.checkTokenExpired(accessToken) ) {
            throw new CustomJWTException(ErrorCode.EXPIRED_TOKEN);
        }

        // refreshToken redis에서 삭제
        log.info("[logout refreshToken delete]");
//        redisRefreshTokenService.deleteRedisDataByAccessToken(accessToken);

        // accessToken redis에 blacklist 등록
        log.info("[logout accessToken blacklist]");
        Long memberId = ((Number) claims.get("id")).longValue();
        Date expDate = new Date(((Number) claims.get("exp")).longValue() * 1000);
        Long leftExpTime = (expDate.getTime() - System.currentTimeMillis()) / (1000 * 60);

//        redisRefreshTokenService.saveBlackList(memberId, accessToken, leftExpTime);
    }
}

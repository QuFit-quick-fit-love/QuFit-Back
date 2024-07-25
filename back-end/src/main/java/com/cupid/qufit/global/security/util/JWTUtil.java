package com.cupid.qufit.global.security.util;

import com.cupid.qufit.global.exception.ErrorCode;
import com.cupid.qufit.global.exception.exceptionType.CustomJWTException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.InvalidClaimException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.InvalidClassException;
import java.io.UnsupportedEncodingException;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Map;
import javax.crypto.SecretKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Log4j2
public class JWTUtil {

    @Value("${ACCESS_TOKEN_EXPIRATION_PERIOD}")
    private Long ACCESS_TOKEN_EXPIRATION_PERIOD;
    @Value("${REFRESH_TOKEN_EXPIRATION_PERIOD}")
    private Long REFRESH_TOKEN_EXPIRATION_PERIOD;
    @Value("${JWT_KEY}")
    private String JWT_KEY;

    /*
     * 토큰 생성
     * */
    public String generateToken(Map<String, Object> valueMap, String type) {
        log.info("---------createToken---------");
        SecretKey key = null;
        try {
            key = Keys.hmacShaKeyFor(JWT_KEY.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

        Long expirationPeriod = ACCESS_TOKEN_EXPIRATION_PERIOD;
        if (type.equals("refresh")) {
            expirationPeriod = REFRESH_TOKEN_EXPIRATION_PERIOD;
        }

        String jwtStr = Jwts.builder()
                            .setHeader(Map.of("type", "JWT"))
                            .setClaims(valueMap)
                            .setIssuedAt(Date.from(ZonedDateTime.now().toInstant()))
                            .setExpiration(Date.from(
                                    ZonedDateTime.now().plusMinutes(expirationPeriod).toInstant()))
                            .signWith(key)
                            .compact();
        return jwtStr;
    }

    /*
     * 토큰 유효성 검사 (expired 제외)
     *
     * - 토큰의 유효기간 만료는 다른 메소드로 관리함
     * */
    public Map<String, Object> validateToken(String token) {

        Map<String, Object> claim = null;

        try {
            SecretKey key = Keys.hmacShaKeyFor(JWT_KEY.getBytes("UTF-8"));

            claim = Jwts.parser()
                        .setSigningKey(key)
                        .parseClaimsJws(token) // 파싱 및 검증, 실패 시 에러
                        .getBody();
        }catch (ExpiredJwtException e){
            throw e;
        } catch (MalformedJwtException e) {
            throw new CustomJWTException(ErrorCode.MALFORMED_TOKEN);
        } catch (UnsupportedJwtException e) {
            throw new CustomJWTException(ErrorCode.UNSUPPORTED_TOKEN);
        } catch (InvalidClaimException invalidClaimException) {
            throw new CustomJWTException(ErrorCode.INVALID_TOKEN);
        } catch (UnsupportedEncodingException e) {
            throw new CustomJWTException(ErrorCode.UNSUPPORTED_ENCODING);
        } catch (Exception e) {
            throw new CustomJWTException(ErrorCode.TOKEN_DEFAULT_ERROR);
        }
        return claim;
    }

    /*
     * 요청 헤더에서 accesstoken 가져옴
     * */
    public String getTokenFromHeader(HttpServletRequest request) {
        String authHeaderStr = request.getHeader("Authorization");

        // access token : Bearer(7자) + JWT 문자열
        if (authHeaderStr == null || authHeaderStr.length() < 7) {
            throw new CustomJWTException(ErrorCode.INVALID_TOKEN);
        }
        return authHeaderStr.substring(7);
    }

    /*
    * * 응답 헤더에 accesstoken 저장
    * */
    public void setTokenToHeader(HttpServletResponse response, String accessToken){
        response.setHeader("Authorization", "Bearer " + accessToken);
    }

    /*
     * * 토큰 만료되었는지 확인
     * */
    public boolean checkTokenExpired(String token) {
        try {
            validateToken(token);
        } catch (ExpiredJwtException e) {
            return true;
        }
        return false;
    }

    /*
     * * 토큰 유효시간 1시간 미만인지 확인
     * */
    public boolean checkTokenExpiringInAnHour(Integer exp) {
        Date expDate = new Date((long) exp * 1000);

        // 현재 시간과의 차이 계산 (min)
        long gap = (expDate.getTime() - System.currentTimeMillis()) / (1000 * 60);

        // 1시간 남았는지 확인
        return gap < 60;
    }
}

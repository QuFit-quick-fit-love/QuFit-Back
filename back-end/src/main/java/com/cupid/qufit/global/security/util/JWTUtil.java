package com.cupid.qufit.global.security.util;

import com.cupid.qufit.global.exception.ErrorCode;
import com.cupid.qufit.global.exception.exceptionType.CustomJWTExeption;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
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

    private static String JWT_KEY;
    private static final Long ACCESS_TOKEN_EXPIRATION_PERIOD = 1800000L; // 30분(임의 설정)

    @Value("${JWT_KEY}")
    public void setJwtKey(String jwtKey) {
        JWT_KEY = jwtKey;
    }

    public static String generateAccessToken(Map<String, Object> valueMap) {
        log.info("---------createToken---------");
        SecretKey key = null;
        try {
            key = Keys.hmacShaKeyFor(JWT_KEY.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

        String jwtStr = Jwts.builder()
                            .setHeader(Map.of("type", "JWT"))
                            .setClaims(valueMap)
                            .setIssuedAt(Date.from(ZonedDateTime.now().toInstant()))
                            .setExpiration(Date.from(
                                    ZonedDateTime.now().plusMinutes(ACCESS_TOKEN_EXPIRATION_PERIOD).toInstant()))
                            .signWith(key)
                            .compact();
        return jwtStr;
    }

    public static Map<String, Object> validateToken(String token) throws JwtException, InvalidClassException {

        Map<String, Object> claim = null;

        try {
            SecretKey key = Keys.hmacShaKeyFor(JWT_KEY.getBytes("UTF-8"));

            claim = Jwts.parser()
                        .setSigningKey(key)
                        .parseClaimsJws(token) // 파싱 및 검증, 실패 시 에러
                        .getBody();

        } catch (ExpiredJwtException expiredJwtException) {
            throw new CustomJWTExeption(ErrorCode.EXPIRED_TOKEN);
        } catch (Exception e) {
            throw new CustomJWTExeption(ErrorCode.TOKEN_DEFAULT_ERROR);
        }
        return claim;
    }
}

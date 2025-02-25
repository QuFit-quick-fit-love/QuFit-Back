package com.cupid.qufit.global.redis.service;

import com.cupid.qufit.global.exception.ErrorCode;
import com.cupid.qufit.global.exception.exceptionType.CustomJWTException;
import com.cupid.qufit.global.redis.component.RedisRefreshToken;
import com.cupid.qufit.global.redis.repository.RedisRefreshTokenRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
@Log4j2
public class RedisRefreshTokenService {

    private final RedisRefreshTokenRepository redisRefreshTokenRepository;
    @Value("${jwt.refresh.expiration}")
    private Long REFRESH_TOKEN_EXPIRATION_PERIOD;

    /*
     * * accesstoken으로 redis에 값을 삭제함
     * */
    public void deleteRedisDataByAccessToken(String accessToken) {
        log.info("[Redis] : deleteRedisData by accesstoken");
        redisRefreshTokenRepository.findByAccessToken(accessToken)
                                   .ifPresent(redisRefreshTokenRepository::delete);
    }

    /*
     * * id로 redis에 값을 삭제함
     * */
    public void deleteRedisDataById(String id) {
        log.info("[Redis] : deleteRedisData by id");
        redisRefreshTokenRepository.findById(id)
                                   .ifPresent(redisRefreshTokenRepository::delete);
    }

    /*
     * * redis에 (키,값)을 생성/수정함
     * */
    public void saveRedisData(Long memberId, String refreshToken, String accessToken) {
        if (!redisRefreshTokenRepository.findById(
                String.valueOf(memberId)).isPresent()) {
            deleteRedisDataById(String.valueOf(memberId));
        }
        log.info("[Redis] : saveRedisData");
        RedisRefreshToken refreshTokenRedis = new RedisRefreshToken(String.valueOf(memberId), refreshToken, accessToken,
                                                                    REFRESH_TOKEN_EXPIRATION_PERIOD * 60);
        redisRefreshTokenRepository.save(refreshTokenRedis);
    }

    /*
     * access token으로 redis에서 값을 가져옴
     * */
    public String getRedisDataByAccessToken(String accessToken) {
        log.info("[Redis] : getRedisData by accesstoken");
        RedisRefreshToken refreshToken = redisRefreshTokenRepository.findByAccessToken(accessToken)
                                                                    .orElseThrow(() -> new CustomJWTException(
                                                                            ErrorCode.REFRESH_TOKEN_NOT_FOUND));
        return refreshToken.getRefreshToken();
    }

    /*
     * * redis에 로그아웃 처리된 access token을 black list로 등록함
     *
     * */
    public void saveBlackList(Long memberId, String accessToken, Long time) {
        log.info("[Redis] : save blackList");
        RedisRefreshToken blackList = new RedisRefreshToken(String.valueOf(memberId), "logout", accessToken, time * 60);
        redisRefreshTokenRepository.save(blackList);
    }
}

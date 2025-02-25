package com.cupid.qufit.global.redis.component;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;
import org.springframework.data.redis.core.index.Indexed;

@AllArgsConstructor
@Getter
@RedisHash(value = "jwtToken")
public class RedisRefreshToken {
    @Id
    private String id;

    private String refreshToken;

    @Indexed
    private String accessToken;

    @TimeToLive
    private Long timeToLive;
}

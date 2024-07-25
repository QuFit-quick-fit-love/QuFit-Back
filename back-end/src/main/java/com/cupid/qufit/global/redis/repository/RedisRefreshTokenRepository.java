package com.cupid.qufit.global.redis.repository;

import com.cupid.qufit.global.redis.component.RedisRefreshToken;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RedisRefreshTokenRepository extends CrudRepository<RedisRefreshToken,String> {
    Optional<RedisRefreshToken> findByAccessToken(String accessToken);
}

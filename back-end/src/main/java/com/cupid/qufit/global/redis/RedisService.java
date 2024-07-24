package com.cupid.qufit.global.redis;

import jakarta.transaction.Transactional;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.data.redis.core.RedisTemplate;

@Service
@RequiredArgsConstructor
@Transactional
public class RedisService {
    private final RedisTemplate<String, String> redisTemplate;

    /*
    * * redis에서 값을 조회함
    * */
    public String getRedisValue(String key){
        return redisTemplate.opsForValue().get(key);
    }

    /*
    * * redis에 값을 삭제함
    * */
    public void deleteRedisValue(String key){
        redisTemplate.delete(key);
    }

    /*
    * * redis에 (키,값)을 생성/수정함
    * */
    public void setRedisData(String key, String value, long time){
        if(getRedisValue(key) != null){
            deleteRedisValue(key);
        }

        Duration expiredDuration = Duration.ofMillis(time);
        redisTemplate.opsForValue().set(key, value, expiredDuration);
    }
}

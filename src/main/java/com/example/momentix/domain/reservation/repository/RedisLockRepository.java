package com.example.momentix.domain.reservation.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Repository;
import java.time.Duration;
import java.util.Collections;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class RedisLockRepository {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ThreadLocal<String> lockHolder = new ThreadLocal<>();

    public boolean lock(String key, Duration timeout) {
        String lockId = UUID.randomUUID().toString();

        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();

        redisScript.setLocation(new ClassPathResource("scripts/acquire_lock.lua"));
        redisScript.setResultType(Long.class);

        Long result = redisTemplate.execute(
                redisScript,
                Collections.singletonList(key),
                lockId, String.valueOf(timeout.getSeconds())
        );

        if (result != null && result == 1) {
            lockHolder.set(lockId);
            return true;
        }
        return false;
    }

    public boolean unlock(String key) {
        String lockId = lockHolder.get();
        if (lockId == null) {
            return false;
        }

        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();

        redisScript.setLocation(new ClassPathResource("scripts/release_lock.lua"));
        redisScript.setResultType(Long.class);

        Long result = redisTemplate.execute(
                redisScript,
                Collections.singletonList(key),
                lockId
        );

        lockHolder.remove();

        return result != null && result == 1;
    }
}
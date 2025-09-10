package com.example.momentix.domain.common.redisController;


import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class RedisTestController {

    private final StringRedisTemplate t;

    @GetMapping("/redis/test")
    public String test() {

        return t.getRequiredConnectionFactory().getConnection().ping();
    }
}

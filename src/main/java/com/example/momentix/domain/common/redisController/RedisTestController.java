package com.example.momentix.domain.common.redisController;


import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

//Redis 연결 시, 확인 Controller
//
// @RestController
//@RequiredArgsConstructor
//public class RedisTestController {
//
//    private final StringRedisTemplate t;
//
//    URL 입력시 Pong 이 나와야 함.
//    @GetMapping("/redis/test")
//    public String test() {
//
//        return t.getRequiredConnectionFactory().getConnection().ping();
//    }
//}

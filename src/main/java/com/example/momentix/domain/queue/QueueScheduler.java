package com.example.momentix.domain.queue;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class QueueScheduler {
    private final QueueService queueService;
    private final RedisTemplate<String, String> redisTemplate;

    public QueueScheduler(QueueService queueService, RedisTemplate<String, String> redisTemplate) {
        this.queueService = queueService;
        this.redisTemplate = redisTemplate;
    }

    /**
     * 1초마다 백그라운드 워커에서 실행
     * 자동으로 예매 가능 상태로 전환 (BatchSize 만큼 여유 잇을 때)
     */
    @Scheduled(fixedRate = 1000)
    public void processQueue() {
        Set<String> eventIds = redisTemplate.opsForSet().members("activeEvent");
        if (eventIds != null) {
            for (String eventId : eventIds) {
                Long activeEventId = Long.parseLong(eventId);
                queueService.processQueue(activeEventId);
            }
        }
    }
}

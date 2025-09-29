package com.example.momentix.domain.queue;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Slf4j
@Component
public class RankConsumer implements StreamListener<String, MapRecord<String, String, String>> {
    private final RedisTemplate<String, String> redisTemplate;
    private final QueueWebSocketHandler webSocketHandler;

    public RankConsumer(RedisTemplate<String, String> redisTemplate, QueueWebSocketHandler webSocketHandler) {
        this.redisTemplate = redisTemplate;
        this.webSocketHandler = webSocketHandler;
    }

    @Override
    public void onMessage(MapRecord<String, String, String> message) {
        String eventId = message.getStream() != null ? String.valueOf(message.getStream().split(":")[1]) : null;
        String token = message.getValue().get("token");
        String status = message.getValue().get("status");
        String position = message.getValue().get("position");
        log.info("onMessage 동작");
        String payload = Map.of(
                "token", token,
                "status", status,
                "position", position
        ).toString();
        if ("WAITING".equals(status)) {
            String sessionId = redisTemplate.opsForValue().get("token:" + eventId + ":" + token);
            if (sessionId != null) {
                try {
                    webSocketHandler.sendMessage(sessionId, payload);
                    redisTemplate.opsForStream().acknowledge(message.getStream(), "alarmQueueGroup" + eventId, message.getId());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                log.info(sessionId, position, payload);
            }
        }
        log.info("token: {} status: {} position: {}", token, status, position);
    }
}

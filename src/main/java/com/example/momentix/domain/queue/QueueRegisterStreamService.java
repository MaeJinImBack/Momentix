package com.example.momentix.domain.queue;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.stream.Consumer;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.ReadOffset;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.stream.StreamMessageListenerContainer;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class QueueRegisterStreamService {
    private final StreamMessageListenerContainer<String, MapRecord<String, String, String>> container;
    private final QueueConsumer queueConsumer;
    private final Map<Long, Boolean> registeredStreams = new ConcurrentHashMap<>();
    private final RedisTemplate<String, String> redisTemplate;

    @PostConstruct
    public void startContainer(){
        if(!container.isRunning()){
            log.info("시작 확인");
            container.start();}
    }

    public void registerStream(Long eventId) {
        if (registeredStreams.containsKey(eventId)) {
            log.info("여기는 있어서 리턴");
            return;
        }

        String streamKey = "stream:" + eventId;

        try{
            redisTemplate.opsForStream().createGroup(streamKey, ReadOffset.from("0"), "eventQueueGroup");
            log.info("그룹생성");
        } catch (Exception e){
            if(e.getMessage() != null && e.getMessage().contains("BUSY GROUP")) {
                log.info("stream already exists");
            } else {
                log.info("error");
                return;
            }
        }

        container.receive(
                Consumer.from("eventQueueGroup", "consumer"+eventId),
                StreamOffset.create(streamKey, ReadOffset.lastConsumed()),
                queueConsumer
        );
        log.info("Stream 등록 확인");
        registeredStreams.put(eventId, true);

    }
}

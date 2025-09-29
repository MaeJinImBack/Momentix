package com.example.momentix.domain.queue;


import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.stream.RecordId;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class QueueService {


    private final RedisTemplate<String, String> redisTemplate;
    private final QueueRegisterStreamService queueRegisterStreamService;

    /**
     * Allowed 상태 유저 수 count
     *
     * @param eventId 공연마다 Allowed 상태 유저 수
     * @return Allowed 상태 유저 수 반환 default : 0
     */
    public Long countAllow(Long eventId) {
        String allowKey = "allow:" + eventId;
        String count = redisTemplate.opsForValue().get(allowKey);
        if (count == null) {
            return 0L;
        }
        return Long.parseLong(count);
    }

    /**
     * 대기열 등록
     *
     * @param sessionId 유저 세션 ID
     * @param eventId   공연 Id
     */
    public void addQueue(String sessionId, Long eventId) {
        // key값 생성 세션 -> 토큰 (공연별 중복 유저 방지용, eventId, sessionId로 구분)
        String sessionToTokenKeyPrefix = "eventSessionId:" + eventId + ":";
        // 기존에 이 공연 대기열에 등록된 유저(Id)면 등록 안함
        if (Boolean.TRUE.equals(redisTemplate.hasKey(sessionToTokenKeyPrefix + sessionId))) {
            return;
        }
        // key값 생성 공연마다 대기열 구분 토큰으로 관리
        String eventQueueKey = "queue:" + eventId;
        // key값 생성 토큰 -> 세션 ID (공연별 알림 발송용, eventId로 구분)
        String tokenToSessionIdKeyPrefix = "token:" + eventId + ":";
        // UUID 값 토큰으로 관리
        String token = UUID.randomUUID().toString();
        // timestamp로 FIFO 구현 + random 값으로 동시에 요청시 우선순위 나누기
        double score = System.currentTimeMillis() + Math.random();

        // token 대기열 배정
        redisTemplate.opsForZSet().add(eventQueueKey, token, score);

        // token -> sessionId 매핑 알림 발송용
        redisTemplate.opsForValue().set(tokenToSessionIdKeyPrefix + token, sessionId, 30, TimeUnit.MINUTES);
        // sessionId -> token 매핑 중복유저 방지용
        redisTemplate.opsForValue().set(sessionToTokenKeyPrefix + sessionId, token, 30, TimeUnit.MINUTES);
    }

    /**
     * 백그라운드워크에서
     * 대기열에서 batchSize 만큼 추출
     *
     * @param eventId 공연Id
     */
    public void processQueue(Long eventId) {
        // key값 생성 공연마다 대기열 구분 토큰으로 관리
        String eventQueueKey = "queue:" + eventId;
        String streamKey = "stream:" + eventId;
        String tokenToSessionIdKeyPrefix = "token:" + eventId + ":";
        String sessionToTokenKeyPrefix = "eventSessionId:" + eventId + ":";
        String allowKey = "allow:" + eventId;

        int batchSize = 3; // 배치사이즈 처리 속도에 따라 동적으로 관리 가능하게 변환 가능성 염두

        int allowSize = countAllow(eventId).intValue();
        if (batchSize - allowSize <= 0) {
            return;
        }
        // batchsize 만큼 조회 // index 값이 0부터 시작해서 batchSize - allowSize -1 해야함
        Set<String> batch = redisTemplate.opsForZSet().range(eventQueueKey, 0, batchSize - allowSize - 1);

        if (batch == null || batch.isEmpty()) {
            return;
        }

        for (String token : batch) {
            // token에 맞는 sessionId값 가져오기
            String sessionId = redisTemplate.opsForValue().get(tokenToSessionIdKeyPrefix + token);
            // sessionId 값 없을경우 continue
            if (sessionId == null) {
                continue;
            }
            // 예매 허용된 상태
            String status = "ALLOWED";
            Map<String, String> msg = Map.of(
                    "token", token,
                    "status", status
            );
            RecordId streamId = redisTemplate.opsForStream().add(streamKey, msg);
            if (streamId != null) {
                redisTemplate.opsForValue().set(token, streamId.getValue(), 10, TimeUnit.MINUTES);
                redisTemplate.opsForValue().increment(allowKey);
            }

            // 완료된 유저 삭제
            redisTemplate.opsForZSet().remove(eventQueueKey, token);
            // token -> sessionId 매핑 알림 발송용 키 삭제
            // sessionId -> token 매핑 중복유저 방지용 키 삭제
            redisTemplate.delete(List.of(sessionToTokenKeyPrefix + sessionId, tokenToSessionIdKeyPrefix + token));
            queueRegisterStreamService.registerStream(eventId);
        }

    }

    /**
     * 대기열 순위 확인
     *
     * @param eventId 공연별로 체크
     * @param token   토큰으로 유저 확인
     */


    public void rankAlarmQueue(Long eventId, String token) {
        String eventQueueKey = "queue:" + eventId;
        String streamRankKey = "streamRank:" + eventId;

        // token의 위치 0부터 시작
        Long position = redisTemplate.opsForZSet().rank(eventQueueKey, token);
        // token의 위치가 null일 경우 return
        if (position == null) {
            return;
        }
        String status = "WAITING"; // 대기상태
        Map<String, String> msg = Map.of(
                "token", token,
                "status", status,
                "position", String.valueOf(position + 1) // 0부터 시작해서 + 1
        );
        redisTemplate.opsForStream().add(streamRankKey, msg);
        redisTemplate.expire(streamRankKey, 30, TimeUnit.MINUTES);
        queueRegisterStreamService.alarmStream(eventId);

    }

    /**
     * 예매 완료시 RedisStream 에서 삭제 후
     * 다음 우선 순위 예매 가능 상태로 만들기
     *
     * @param eventId 공연별 확인
     * @param token   유저 확인용
     */
    public void completeQueue(Long eventId, String token) {
        String streamKey = "stream:" + eventId;
        String allowKey = "allow:" + eventId;

        String streamId = redisTemplate.opsForValue().get(token);

        if (streamId != null) {
            redisTemplate.opsForStream().delete(streamKey, streamId);
            redisTemplate.delete(token);
            redisTemplate.opsForValue().decrement(allowKey);
        }
        processQueue(eventId);

    }

}

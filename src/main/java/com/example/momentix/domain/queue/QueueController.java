package com.example.momentix.domain.queue;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequiredArgsConstructor
@RequestMapping("/queue")
public class QueueController {

    private final QueueService queueService;

    @GetMapping("/{eventTimeId}/turn")
    public SseEmitter queueMyTurn(@PathVariable Long eventTimeId, HttpSession session) {
        SseEmitter emitter = new SseEmitter();
        // 대기열 Key
        String queueKey = "queue:" + eventTimeId;
        // 예매 가능한 상태의 대기열 Key
        String reserveKey = "reserve:" + queueKey;
        String sessionId = session.getId();

        return emitter;
    }

    @PostMapping("/test/{eventId}")
    public ResponseEntity<String> addQueue(@PathVariable Long eventId, @RequestParam String sessionId) {
        queueService.addQueue(sessionId, eventId);
        return ResponseEntity.ok("대기열에 등록되었습니다.");
    }

    @PostMapping("/test/rank/{eventId}")
    public ResponseEntity<String> rankQueue(@PathVariable Long eventId, @RequestParam String token) {
        queueService.rankAlarmQueue(eventId, token);
        return ResponseEntity.ok("대기열 순위 확인");
    }

    @PostMapping("/test/process/{eventId}")
    public ResponseEntity<String> processQueue(@PathVariable Long eventId) {
        queueService.processQueue(eventId);
        return ResponseEntity.ok("대기열 변경 확인");
    }

    @PostMapping("/test/end/{eventId}")
    public ResponseEntity<String> endQueue(@PathVariable Long eventId, @RequestParam String token) {
        queueService.completeQueue(eventId, token);
        return ResponseEntity.ok("예매 완료, 대기열에서 삭제");
    }
}

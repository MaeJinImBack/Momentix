package com.example.momentix.domain.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 서비스에서 던진 RuntimeException을 잡아 HTTP 상태 코드로 변환
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(RuntimeException ex) {
        // 서비스에서 던진 특정 메시지를 포함하는 경우에만 409 Conflict 응답
        if (ex.getMessage().contains("이미 다른 사용자가 선택")) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
        }
        // 그 외 다른 RuntimeException은 500 서버 에러로 처리
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 내부 오류가 발생했습니다.");
    }

    // IllegalStateException 처리 (hold() 메서드 내부에서 발생 가능)
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<String> handleIllegalStateException(IllegalStateException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }

}

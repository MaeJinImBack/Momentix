package com.example.momentix.domain.paymenthistory.controller;

import com.example.momentix.domain.auth.impl.UserDetailsImpl;
import com.example.momentix.domain.paymenthistory.dto.PaymentConfirmRequest;
import com.example.momentix.domain.paymenthistory.dto.PaymentCreateRequest;
import com.example.momentix.domain.paymenthistory.dto.PaymentResponse;
import com.example.momentix.domain.paymenthistory.service.PaymentHistoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payment")
public class PaymentHistoryController {
    private final PaymentHistoryService paymentHistoryService;

    public PaymentHistoryController(PaymentHistoryService paymentHistoryService) {
        this.paymentHistoryService = paymentHistoryService;
    }

    //결제 대기
    @PostMapping
    public ResponseEntity<PaymentResponse> create(
            @AuthenticationPrincipal UserDetailsImpl user,
            @RequestBody PaymentCreateRequest req
    ) {
        return ResponseEntity.ok(paymentHistoryService.create(user.getUserId(), req));
    }

    //결제 확정
    @PostMapping("/{paymentId}/confirm")
    public ResponseEntity<PaymentResponse> confirm(
            @AuthenticationPrincipal UserDetailsImpl user,
            @PathVariable Long paymentId,
            @RequestBody PaymentConfirmRequest req
    ) {
        return ResponseEntity.ok(paymentHistoryService.confirm(user.getUserId(), paymentId, req));
    }
    // 결제 수정 =???

    //결제 조회 =/payments/{paymentId}

    //결제 삭제 =/payments/{paymentId}/cancel
}

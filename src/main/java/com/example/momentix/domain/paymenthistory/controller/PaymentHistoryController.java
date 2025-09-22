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

    //결제 확정(= 생성 + 티켓발급 + 연결까지 한 번에)
    @PostMapping
    public ResponseEntity<PaymentResponse> confirm(
            @AuthenticationPrincipal UserDetailsImpl user,
            @RequestBody PaymentConfirmRequest request
    ) {
        PaymentResponse res = paymentHistoryService.confirmSimple(user.getUserId(), request);
        return ResponseEntity.ok(res);
    }
    // 결제 수정 =???

    //결제 조회 =/payments/{paymentId}

    //결제 삭제 =/payments/{paymentId}/cancel
}

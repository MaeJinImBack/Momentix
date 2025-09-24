package com.example.momentix.domain.point.controller;


import com.example.momentix.domain.auth.impl.UserDetailsImpl;
import com.example.momentix.domain.point.dto.PaymentByPaymentRequest;
import com.example.momentix.domain.point.dto.PaymentEarnByPaymentRequest;
import com.example.momentix.domain.point.dto.PointApplyRequest;
import com.example.momentix.domain.point.dto.PointBalanceResponse;
import com.example.momentix.domain.point.service.PointService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/users/points")
public class PointController {

    private final PointService pointService;

    public PointController(PointService pointService) {
        this.pointService = pointService;
    }

    // 내 포인트 조회
    @GetMapping("/me")
    public ResponseEntity<PointBalanceResponse> me(@AuthenticationPrincipal UserDetailsImpl user) {
        return ResponseEntity.ok(pointService.getMyPoints(user.getUserId()));
    }

    // 즉시 적립
    @PostMapping("/earn")
    public ResponseEntity<PointBalanceResponse> earn(
            @AuthenticationPrincipal UserDetailsImpl user,
            @RequestBody PointApplyRequest pointApplyRequest
    ){
        return ResponseEntity.ok(pointService.earn(user.getUserId(), pointApplyRequest.getIdempotencyKey(),
                pointApplyRequest.getAmount(), pointApplyRequest.getReason(), pointApplyRequest.getPaymentId(),
                pointApplyRequest.getReservationId()));
    }

    // 사용(차감)
    @PostMapping("/use")
    public ResponseEntity<PointBalanceResponse> use(
            @AuthenticationPrincipal UserDetailsImpl user,
            @RequestBody PointApplyRequest pointApplyRequest
    ){
        return ResponseEntity.ok(pointService.use(user.getUserId(), pointApplyRequest.getIdempotencyKey(),
                pointApplyRequest.getAmount(), pointApplyRequest.getReason(), pointApplyRequest.getPaymentId(),
                pointApplyRequest.getReservationId()));
    }

    // 적립 에정
    @PostMapping("/pending/earn")
    public ResponseEntity<PointBalanceResponse> earnPending(
            @AuthenticationPrincipal UserDetailsImpl user,
            @RequestBody PointApplyRequest pointApplyRequest
    ){
        return ResponseEntity.ok(pointService.earnPending(user.getUserId(), pointApplyRequest.getIdempotencyKey(),
                pointApplyRequest.getAmount(), pointApplyRequest.getReason(), pointApplyRequest.getPaymentId(),
                pointApplyRequest.getReservationId()));
    }

    //예정 해제
    @PostMapping("/pending/release")
    public ResponseEntity<PointBalanceResponse> releasePending(
            @AuthenticationPrincipal UserDetailsImpl user,
            @RequestBody PointApplyRequest pointApplyRequest
    ){
        return ResponseEntity.ok(pointService.releasePending(user.getUserId(), pointApplyRequest.getIdempotencyKey(),
                pointApplyRequest.getAmount(), pointApplyRequest.getReason(), pointApplyRequest.getPaymentId(),
                pointApplyRequest.getReservationId()));
    }

    // ----------------여기서부터 결제 연동(결제직후 적립/환불 불가/결제 취소/포인트 사용된 결제 취소)--------------
// 1) 결제 성공 직후: 할인 후 결제금액의 3%를 "적립 예정"으로 쌓기
    @PostMapping("/pending/earn-by-payment")
    public ResponseEntity<PointBalanceResponse> earnPendingByPayment(
            @AuthenticationPrincipal UserDetailsImpl user,
            @RequestBody PaymentEarnByPaymentRequest paymentEarnByPaymentRequest
    ){
        return ResponseEntity.ok(pointService.earnPendingByPaymentAmount(user.getUserId(), paymentEarnByPaymentRequest.getIdempotencyKey(),
                paymentEarnByPaymentRequest.getPaymentId(), paymentEarnByPaymentRequest.getReservationId(), paymentEarnByPaymentRequest.getDiscountedAmount(), "결제 적립 예정(3%)"));
    }

    @PostMapping("/pending/release-by-payment")
    public ResponseEntity<PointBalanceResponse> releasePendingByPayment(
            @AuthenticationPrincipal UserDetailsImpl user,
            @RequestBody PaymentByPaymentRequest paymentByPaymentRequest
    ){
        return ResponseEntity.ok(pointService.releasePendingByPayment(user.getUserId(), paymentByPaymentRequest.getIdempotencyKey(),
                paymentByPaymentRequest.getPaymentId(), "환불 불가 시점 적립 확정"));
    }

    @PostMapping("/pending/cancel-by-payment")
    public ResponseEntity<PointBalanceResponse> cancelPendingByPayment(
            @AuthenticationPrincipal UserDetailsImpl user,
            @RequestBody PaymentByPaymentRequest paymentByPaymentRequest
    ){
        return ResponseEntity.ok(pointService.cancelPendingForPayment(user.getUserId(), paymentByPaymentRequest.getIdempotencyKey(),
                paymentByPaymentRequest.getPaymentId(), "결제 취소로 적립 예정 취소"));
    }

    @PostMapping("/refund-used-by-payment")
    public ResponseEntity<PointBalanceResponse> refundUsedByPayment(
            @AuthenticationPrincipal UserDetailsImpl user,
            @RequestBody PaymentByPaymentRequest paymentByPaymentRequest
    ) {
        return ResponseEntity.ok(pointService.refundUsedPointsForPayment(user.getUserId(), paymentByPaymentRequest.getIdempotencyKey(),
                paymentByPaymentRequest.getPaymentId(), "결제 취소로 포인트 사용 환급"));
    }
}

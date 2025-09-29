package com.example.momentix.domain.paymenthistory.entity;

public enum PaymentStatusType {

    PENDING("결제 대기"),
    SUCCESS("결제 성공"),// 결제 확정(티켓 발급 트리거)
    FAILED("결제 실패"),
    CANCEL("결제 취소");// 사용자가 취소



    private final String korean;

     PaymentStatusType(String korean){this.korean = korean;
    }

}

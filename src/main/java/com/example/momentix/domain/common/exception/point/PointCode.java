package com.example.momentix.domain.common.exception.point;


public enum PointCode {

    INVALID_POINT_AMOUNT(400, "포인트는 0보다 커야 합니다."),
    INSUFFICIENT_POINTS(400, "보유 포인트가 부족합니다."),
    INSUFFICIENT_PENDING_POINTS(400, "예정 포인트가 부족합니다."),
    POINT_TRANSACTION_NOT_FOUND(404, "포인트 거래 내역을 찾을 수 없습니다."),
    POINTS_ALREADY_EXPIRED(409, "이미 만료된 포인트입니다."),
    REFUND_PERIOD_EXPIRED(409, "포인트 환불 가능 기간이 지났습니다.");

    private final int status;
    private final String message;

    PointCode(int status, String message){
        this.status=status;
        this.message=message;
    }

    public int getStatus(){
        return status;
    }
    public String getMessage(){
        return message;
    }
}

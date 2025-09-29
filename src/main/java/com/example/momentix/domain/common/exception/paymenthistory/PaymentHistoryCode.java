package com.example.momentix.domain.common.exception.paymenthistory;



public enum PaymentHistoryCode {
    PAYMENT_RESERVATION_MISMATCH(400, "예약 정보가 결제와 일치하지 않습니다."),
    PAYMENT_ALREADY_PENDING(409, "이미 대기 중(PENDING)인 결제가 있습니다."),
    PAYMENT_NO_SUCCESS(409, "결제가 성공(SUCCESS) 상태가 아닙니다."),
    PAYMENT_NOT_FOUND(404, "결제 내역이 없습니다."),
    TICKET_PAYMENT_LINK_FAILED(500, "티켓 결제 연결 실패"),
    TICKET_NOT_FOUND(404, "티켓을 찾을 수 없습니다."),
    PAYMENT_FORBIDDEN(403, "본인 결제가 아닙니다."),
    TICKET_PAYMENT_UNLINK_FAILED(500, "티켓 결제 연결 해제 실패"),
    DUPLEICATED_REQUEST(404,"중복 요청입니다.");

    private final int status;
    private final String message;

    PaymentHistoryCode(int status, String message){
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

package com.example.momentix.domain.common.exception.ticket;


public enum TicketCode {


    INVALID_TICKET_STATUS(400, "잘못된 티켓 상태 값입니다."),
    FORBIDDEN(403, "권한이 없습니다."),
    NO_PERMISSION(403, "해당 티켓에 대한 권한이 없습니다."),
    RESERVATION_NOT_FOUND(404,"존재하지 않는 예매 정보입니다."),
    TICKET_NOT_FOUND(404, "해당 티켓 내역을 찾을 수 없습니다.");

    private final int status;
    private final String message;

    TicketCode(int status, String message){
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

package com.example.momentix.domain.common.exception.QueueRegisterStream;

public enum QueueRegisterStreamException {
    EVENT_NOT_OPEN_FOR_BOOKING(409, "아직 예매가 시작되지 않은 공연입니다.");

    private final int status;
    private final String message;

    QueueRegisterStreamException(int status, String message){
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

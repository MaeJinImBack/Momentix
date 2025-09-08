package com.example.momentix.domain.ticket.entity;

public enum TicketStatusType {

    WAITING_PAYMENT,
    COMPLETED_PAYMENT,
    CANCEL_TICKET;



    public String toKorean(TicketStatusType ticketStatusType) {

        return switch (ticketStatusType) {
            case CANCEL_TICKET -> "예매취소";
            case WAITING_PAYMENT -> "결제대기";
            case COMPLETED_PAYMENT -> "결제완료";
        };
    }

}

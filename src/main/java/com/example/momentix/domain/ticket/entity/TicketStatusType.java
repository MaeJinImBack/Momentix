

package com.example.momentix.domain.ticket.entity;

public enum TicketStatusType {

    COMPLETED_PAYMENT, // 결제완료
    CANCEL_TICKET,     // 예매취소
    NOSHOW,            // 노쇼
    COMPLETED_VIEWING; // 관람완료

    public String toKorean(TicketStatusType ticketStatusType) {

        return switch (ticketStatusType) {
            case CANCEL_TICKET -> "예매취소";
            case COMPLETED_PAYMENT -> "결제완료";
            case NOSHOW -> "노쇼";
            case COMPLETED_VIEWING -> "관람완료";
        };
    }
}
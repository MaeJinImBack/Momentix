package com.example.momentix.domain.reservation.entity;

public enum ReservationStatusType {
// 예약상태
    DRAFT, //생성
    SELECT_PLACE, // 공연 장소 선택 중
    SELECT_TIME, // 시간 선택 중
    SELECT_SEAT, // 좌석 선택 중
    WAIT_PAYMENT, // 결제 대기
    CANCELED,//사용자가 취소
    COMPLETED_TICKET//Ticket 발부
}

package com.example.momentix.domain.ticket.entity;

import com.example.momentix.domain.common.entity.TimeStamped;
import com.example.momentix.domain.events.entity.eventtimes.EventTimes;
import com.example.momentix.domain.events.entity.seats.Seats;
import com.example.momentix.domain.paymenthistory.entity.PaymentHistory;
import com.example.momentix.domain.users.entity.Users;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Getter
@Entity
@Table(name = "tickets",
        uniqueConstraints = {
                @UniqueConstraint(name = "event_time_user", columnNames = {"event_time_id", "user_id"}), // 1인 1매
                @UniqueConstraint(name = "event_time_seat", columnNames = {"event_time_id", "seat_id"})  // 좌석 더블부킹 방지
        }
)
@NoArgsConstructor
public class Tickets extends TimeStamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ticketId;

    @Column(nullable = false, unique = true, length = 50)
    private String ticketNumber;


    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TicketStatusType ticketStatusType;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private Users users;
    //
//    @ManyToOne(fetch = FetchType.LAZY, optional = false)
//    @JoinColumn(name = "event_time_id")
    @Column(name = "event_time_id", nullable = false)
    private Long eventTimeId;
    // 공연 시작 시간
    @Column(nullable = false)
    private LocalDateTime eventStartTime;

    // 공연 종료 시간
    @Column(nullable = false)
    private LocalDateTime eventEndTime;

    //전시는 구현 전이라 optional = false
//    @ManyToOne(fetch = FetchType.LAZY, optional = false)
//    @JoinColumn(name =  "seat_id")
    @Column(name = "seat_id", nullable = false)
    private Long seatId;
    // 좌석 행 열
    @Column(nullable = false)
    private Long seatRow;
    @Column(nullable = false)
    private Long seatCol;


    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "payment_history_id", nullable = true)
    private PaymentHistory paymentHistory;

    @Column(nullable = false)
    private boolean isDeleted = false;

    //== 생성자 ==//
    public Tickets(Users user, Seats seat, EventTimes eventTime, String ticketNumber) {
        this.users = user;
        this.seatId = seat.getId();
        this.seatRow = seat.getSeatRow();
        this.seatCol = seat.getSeatCol();
        this.eventTimeId = eventTime.getId();
        this.eventStartTime = eventTime.getEventStartTime();
        this.eventEndTime = eventTime.getEventEndTime();
        this.ticketNumber = ticketNumber;
        this.ticketStatusType = TicketStatusType.COMPLETED_PAYMENT;
    }

    //== 상태 변경 메소드, 티켓 결제 취소 (Soft Delete 사용자용) ==//
    public void updateStatus(TicketStatusType status) {
        this.ticketStatusType = status;
    }

    //== 상태 변경 메소드, 티켓 내역 삭제 (Soft Delete 관리자용) ==//
    public void softDelete() {
        this.isDeleted = true;
    }

}

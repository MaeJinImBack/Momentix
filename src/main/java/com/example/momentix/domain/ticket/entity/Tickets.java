package com.example.momentix.domain.ticket.entity;

import com.example.momentix.domain.common.entity.TimeStamped;
import com.example.momentix.domain.events.entity.eventtimes.EventTimes;
import com.example.momentix.domain.events.entity.seats.Seats;
import com.example.momentix.domain.paymenthistory.entity.PaymentHistory;
import com.example.momentix.domain.users.entity.Users;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;


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

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "event_time_id")
    private EventTimes eventTime;

    //전시는 구현 전이라 optional = false
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name =  "seat_id")
    private Seats seat;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "history_id")
    private PaymentHistory paymentHistory;

    @Column(nullable = false)
    private boolean isDeleted = false;

    //== 생성자 ==//
    public Tickets(Users user, Seats seat, EventTimes eventTime, String ticketNumber) {
        this.users = user;
        this.seat = seat;
        this.eventTime = eventTime;
        this.ticketNumber = ticketNumber;
        this.ticketStatusType = TicketStatusType.WAITING_PAYMENT;
    }

    //== 상태 변경 메소드 (Soft Delete 용) ==//
    public void softDelete() {
        this.isDeleted = true;
    }
}

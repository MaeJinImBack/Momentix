package com.example.momentix.domain.ticket.entity;

import com.example.momentix.domain.common.entity.TimeStamped;
import com.example.momentix.domain.events.entity.eventtimes.EventTimes;
import com.example.momentix.domain.events.entity.seats.Seats;
import com.example.momentix.domain.paymenthistory.entity.PaymentHistory;
import com.example.momentix.domain.users.entity.Users;
import jakarta.persistence.*;

@Entity
@Table(name = "tickets",
    uniqueConstraints = {
     @UniqueConstraint(name = "event_time_user", columnNames = {"event_time_id", "user_id"}), // 1인 1매
     @UniqueConstraint(name = "event_time_seat", columnNames = {"event_time_id", "seat_id"}) // 좌석 더블부킹 방지
    }
)

public class Tickets extends TimeStamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ticketId;

    @Column(name = "ticket_number", nullable = false, unique = true, length = 19)
    private String ticketNumber;


    @Enumerated(EnumType.STRING)
    @Column(name = "ticket_status", nullable = false)
    private TicketStatusType ticketStatusType;

    //추후에 수정 가능성 있음
    @Column
    private Long eventId;

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

}

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
     @UniqueConstraint(name = "eventTimeUser", columnNames = {"eventTimeId", "userId"}), // 1인 1매
     @UniqueConstraint(name = "eventTimeSeat", columnNames = {"eventTimeId", "seatId"}) // 좌석 더블부킹 방지
    }
)

public class Tickets extends TimeStamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ticketId;

    @Column(name = "ticketNumber", nullable = false, unique = true, length = 19)
    private String ticketNumber;


    @Enumerated(EnumType.STRING)
    @Column(name = "ticketStatus", nullable = false)
    private TicketStatusType ticketStatusType;

    //추후에 수정 가능성 있음
    @Column
    private Long eventId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "userId")
    private Users users;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "eventTimeId")
    private EventTimes eventTime;

    //전시는 구현 전이라 optional = false
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name =  "seatId")
    private Seats seat;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "historyId")
    private PaymentHistory paymentHistory;

}

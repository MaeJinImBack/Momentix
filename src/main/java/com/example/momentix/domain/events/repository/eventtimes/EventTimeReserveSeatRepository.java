package com.example.momentix.domain.events.repository.eventtimes;

import com.example.momentix.domain.events.entity.eventtimes.EventTimeReserveSeat;
import com.example.momentix.domain.reservation.entity.SeatStatusType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface EventTimeReserveSeatRepository
        extends JpaRepository<EventTimeReserveSeat, Long>, EventTimeReserveSeatRepositoryCustom {
    @Query("""
        select r
        from EventTimeReserveSeat r
        where r.eventTimes.id = :eventTimeId
          and r.eventSeat.id = :eventSeatId
        """)
    Optional<EventTimeReserveSeat> findByEventTimes_Id(
            @Param("eventTimeId") Long eventTimeId,
            @Param("eventSeatId") Long eventSeatId
    );

    // 상태 포함 조회 (원하면 사용)
    @Query("""
        select r
        from EventTimeReserveSeat r
        where r.eventTimes.id = :eventTimeId
          and r.eventSeat.id = :eventSeatId
          and r.seatReserveStatus = :status
        """)
    Optional<EventTimeReserveSeat> findByEventTimes_Id(
            @Param("eventTimeId") Long eventTimeId,
            @Param("eventSeatId") Long eventSeatId,
            @Param("status") SeatStatusType status
    );
}

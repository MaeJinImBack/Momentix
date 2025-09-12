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

    // (event_time_id, event_seat_id) 조합으로 조회
    Optional<EventTimeReserveSeat>
    findByEventTimes_IdAndEventSeat_Id(Long eventTimeId, Long eventSeatId);

    // 상태까지 조건에 포함해 조회 (필요할 때만 사용)
    Optional<EventTimeReserveSeat>
    findByEventTimes_IdAndEventSeat_IdAndSeatReserveStatus(
            Long eventTimeId, Long eventSeatId, SeatStatusType status);
}

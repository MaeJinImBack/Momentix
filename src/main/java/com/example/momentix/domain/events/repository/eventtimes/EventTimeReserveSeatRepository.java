package com.example.momentix.domain.events.repository.eventtimes;

import com.example.momentix.domain.events.entity.enums.SeatStatusType;
import com.example.momentix.domain.events.entity.eventtimes.EventTimeReserveSeat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

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


    // enum 매핑 피해서 "ID만" 뽑기(스칼라 조회라 매핑 예외 안 남)
    @Query("""
        select r.id
        from EventTimeReserveSeat r
        where r.eventTimes.id = :eventTimeId
          and r.eventSeat.id = :eventSeatId
        """)
    Optional<Long> findIdOnlyByEventTimes_IdAndEventSeat_Id(
            @Param("eventTimeId") Long eventTimeId,
            @Param("eventSeatId") Long eventSeatId);

    @Modifying
    @Transactional
    @Query("""
        update EventTimeReserveSeat r
           set r.seatReserveStatus = :next
         where r.id = :id
           and r.seatReserveStatus = :expected
        """)
    int casUpdateStatus(@Param("id") Long id,
                        @Param("expected") SeatStatusType expected,
                        @Param("next") SeatStatusType next);

    @Modifying
    @Transactional
    @Query("""
        update EventTimeReserveSeat r
           set r.seatReserveStatus = :next
         where r.id = :id
        """)
    int forceUpdateStatus(@Param("id") Long id,
                          @Param("next") SeatStatusType next);

    @Modifying(clearAutomatically = false, flushAutomatically = true)
    @Transactional
    @Query(value = """
        update event_time_reserve_seat
           set seat_reserve_status = 'AVAILABLE'
         where (seat_reserve_status is null or seat_reserve_status = '')
           and event_time_id = :eventTimeId
           and event_seat_id = :eventSeatId
        """, nativeQuery = true)
    int normalizeIfBlank(@Param("eventTimeId") Long eventTimeId,
                         @Param("eventSeatId") Long eventSeatId);
}

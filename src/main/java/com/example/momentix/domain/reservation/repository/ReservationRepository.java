package com.example.momentix.domain.reservation.repository;


import com.example.momentix.domain.reservation.entity.ReservationStatusType;
import com.example.momentix.domain.reservation.entity.Reservations;
import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservations, Long> {

    @Query("select r from Reservations r " +
            "where r.users.userId = :usersId" +
            " and r.events.id = :eventsId" +
            " and r.reservationStatusType in (:active)" +
            " order by r.reservationId desc")
    Optional<Reservations> findActiveByUsers_UsersIdAndEvents_Id(
            @Param("usersId") Long usersId,
            @Param("eventsId") Long eventsId,
            @Param("active") List<ReservationStatusType> draft);

    @EntityGraph(attributePaths = {"events"})
    Optional<Reservations> findByReservationIdAndUsers_UserId(Long reservationId, Long userId);

    @EntityGraph(attributePaths = {"events"})
    List<Reservations> findByUsers_UserIdOrderByReservationIdDesc(Long userId);

    // 결제 - 동시성 이슈 고려함(비관적 락: PK 경로를 reservationId로)
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints(@QueryHint(name = "jakarta.persistence.lock.timeout", value = "5000")) // 5초 대기 (DB별 지원)
    @Query("select reservations from Reservations reservations where reservations.reservationId = :id")
    Optional<Reservations> findByIdForUpdate(@Param("id") Long id);
}

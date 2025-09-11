package com.example.momentix.domain.reservation.repository;


import com.example.momentix.domain.reservation.entitiy.ReservationStatusType;
import com.example.momentix.domain.reservation.entitiy.Reservations;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservations, Long> {


    @Query("select r from Reservations r " +
            "where r.users.userId = :usersId" +
            " and r.events.id = :eventsId" +
            " and r.reservationStatusType in (:active)" +
            " order by r.reservationId desc")
    List<Reservations> findActiveByUsers_UsersIdAndEvents_Id(
            @Param("usersId")Long usersId,
            @Param("eventsId")Long eventsId,
            @Param("active")List<ReservationStatusType> draft);


}

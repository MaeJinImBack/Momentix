package com.example.momentix.domain.paymenthistory.repository;

import com.example.momentix.domain.paymenthistory.entity.PaymentHistory;
import com.example.momentix.domain.paymenthistory.entity.PaymentStatusType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentHistoryRepository extends JpaRepository<PaymentHistory, Long> {

    @Query("""
           select (count(ph) > 0)
             from PaymentHistory ph
            where ph.reservationId = :reservationId
              and ph.paymentStatusType = com.example.momentix.domain.paymenthistory.entity.PaymentStatusType.PENDING
           """)
    boolean existsPendingByReservation(@Param("reservationId") Long reservationId);
}
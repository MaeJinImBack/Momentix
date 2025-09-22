package com.example.momentix.domain.paymenthistory.repository;

import com.example.momentix.domain.paymenthistory.entity.PaymentHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentHistoryRepository extends JpaRepository<PaymentHistory, Long> {

    @Query("""
           select (count(paymentHistory) > 0)
             from PaymentHistory paymentHistory
            where paymentHistory.reservationId = :reservationId
              and paymentHistory.paymentStatusType = com.example.momentix.domain.paymenthistory.entity.PaymentStatusType.PENDING
           """)
    boolean existsPendingByReservation(@Param("reservationId") Long reservationId);
}
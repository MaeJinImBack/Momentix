package com.example.momentix.domain.paymenthistory.repository;

import com.example.momentix.domain.paymenthistory.entity.PaymentHistory;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentHistoryRepository extends JpaRepository<PaymentHistory, Long> {

    // 상태 유니크 제약으로 DB가 막고, 조회는 참고용으로만 사용
    @Query("""
           select (count(paymentHistory) > 0)
             from PaymentHistory paymentHistory
            where paymentHistory.reservationId = :reservationId
              and paymentHistory.paymentStatusType = com.example.momentix.domain.paymenthistory.entity.PaymentStatusType.PENDING
           """)
    boolean existsPendingByReservation(@Param("reservationId") Long reservationId);

    Optional<PaymentHistory> findByReservationIdAndIdempotencyKey(Long reservationId, String key);


    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select ph from PaymentHistory ph where ph.paymentHistoryId = :id")
    Optional<PaymentHistory> findByIdForUpdate(@Param("id") Long id);
}
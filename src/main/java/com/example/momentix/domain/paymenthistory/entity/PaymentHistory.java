package com.example.momentix.domain.paymenthistory.entity;

import com.example.momentix.domain.common.entity.TimeStamped;
import jakarta.persistence.*;

import java.math.BigDecimal;


@Entity
public class PaymentHistory extends TimeStamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paymentHistoryId;

    @Column(name = "payer")
    private String payer;

    @Column(name = "payment_method")
    private String paymentMethod;

    @Column(name = "payment_price")
    private BigDecimal paymentPrice;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status")
    private PaymentStatusType paymentStatusType;
}

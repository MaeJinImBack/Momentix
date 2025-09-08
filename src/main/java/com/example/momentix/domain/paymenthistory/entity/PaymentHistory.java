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

    @Column(name = "paymentMethod")
    private String paymentMethod;

    @Column(name = "paymentPrice")
    private BigDecimal paymentPrice;

    @Enumerated(EnumType.STRING)
    @Column(name = "paymentStatus")
    private PaymentStatusType paymentStatusType;
}

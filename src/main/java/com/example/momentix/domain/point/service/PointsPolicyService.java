package com.example.momentix.domain.point.service;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class PointsPolicyService {
    // 발행주체 정책
    // 할인 후 3% 적립/ 유효기간 없음/ 적립 시점: 환불 불가 시점 즉시

    private static final BigDecimal RATE = new BigDecimal("0.03");

    public long calculateEarnPoints(BigDecimal discountedAmount) {
        if (discountedAmount == null || discountedAmount.signum() < 0) {
            throw new IllegalArgumentException("음수가 될 수 없습니다.");
        }
        return discountedAmount.multiply(RATE).setScale(0, RoundingMode.DOWN).longValueExact();
    }
}

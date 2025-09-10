package com.example.momentix.domain.common.util;

import java.time.LocalDate;
import java.time.ZoneId;

//성인인증
// 다른 요청에서도 가능하게 common!
// 나이계산 유틸
public class AgeUtil {
    private static final ZoneId zoneId = ZoneId.of("Asia/Seoul");// 한국나이 기준임

    public static boolean isAdult(LocalDate birthDate) {
        LocalDate today=LocalDate.now(zoneId);
        return !today.isBefore(birthDate.plusYears(19));
    }
    
}

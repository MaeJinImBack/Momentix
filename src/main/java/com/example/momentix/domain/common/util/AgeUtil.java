package com.example.momentix.domain.common.util;

import java.time.LocalDate;
import java.time.ZoneId;

//성인인증
// 공연조회, 예매, 인증 등 모든 모듈에서 재사용 가능.
// 나이계산 유틸
public class AgeUtil {
    private static final ZoneId zoneId = ZoneId.of("Asia/Seoul");// 한국나이 기준임

    // LocalDate birthDate : DB 값 그대로 가져옴
    // int age : 우리가 만드는 값
    public static boolean isOverAge(LocalDate birthDate, int age) {
        if (birthDate == null) return false; //생년월일null이면 미성년자로 간주.
        LocalDate today = LocalDate.now(zoneId);//지금 시점의 오늘 날짜(연·월·일)만 가져오기
        return !today.isBefore(birthDate.plusYears(age));// birthDate = 2007-09-10, age = 19 -> 2026-09-10 (만 19세 되는 날)
    }
}

// 사용 예시
// 성인 여부 확인 (만 19세 이상)
//if (!AgeUtil.isOverAge(user.getBirthDate(), 19)) {
//        throw new ForbiddenException("19세 미만은 예매 불가");
//}
//
//번외, 시니어 할인 (만 65세 이상)
//        if (AgeUtil.isOverAge(user.getBirthDate(), 65)) {
//        applySeniorDiscount();
//}
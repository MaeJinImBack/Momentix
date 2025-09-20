package com.example.momentix.domain.search.dto;


import java.time.LocalDateTime;

// 시간대별 문서 개수(검색 로그 개수) 한 칸
public class HourlyCountBucket {
    private LocalDateTime hour;//(Asia/Seoul 기준)
    private long count;// 그 시간대에 몇 건인지

    public HourlyCountBucket() {}

    public HourlyCountBucket(LocalDateTime hour, long count) {
        this.hour = hour;
        this.count = count;
    }

    public LocalDateTime getHour() { return hour; }
    public void setHour(LocalDateTime hour) { this.hour = hour; }
    public long getCount() { return count; }
    public void setCount(long count) { this.count = count; }
}
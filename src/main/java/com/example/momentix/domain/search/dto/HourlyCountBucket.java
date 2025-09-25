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

    //콘솔에 스케줄러 잘 찍히는지 확인용
    @Override
    public String toString() {
        return "Hour: " + hour + ", Count: " + count;
    }

    public LocalDateTime getHour() { return hour; }
    public void setHour(LocalDateTime hour) { this.hour = hour; }
    public long getCount() { return count; }
    public void setCount(long count) { this.count = count; }
}
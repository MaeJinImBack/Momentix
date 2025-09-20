package com.example.momentix.domain.common.elasticsearch;

// 상수 모음(객체로 절대 만들지 말 것)
public final class IndexNames {
    private IndexNames() {}

    //이벤트 데이터가 저장되는 인덱스 이름
    public static final String EVENTS = "events";

    // 이벤트 문서의 필드 이름들
    public static final String FIELD_EVENT_TITLE = "eventTitle"; // 공연 제목
    public static final String FIELD_EVENT_TITLE_SUGGEST = "eventTitle_suggest"; // 제목 자동완성용 필드
    public static final String FIELD_PLACE_NAME = "placeName";// 공연장 이름
    public static final String FIELD_PLACE_NAME_SUGGEST = "placeName_suggest";// 공연장 자동완성용 필드
    public static final String FIELD_PLACE_ADDR = "placeAddress"; // 공연장 주소
    public static final String FIELD_CATEGORY = "eventCategory"; // 공연 카테고리
    public static final String FIELD_START = "eventStartDate"; // 공연 시작일
    public static final String FIELD_END = "eventEndDate"; // 공연 종료일

    // 검색 로그 관련
    public static final String SEARCH_LOGS_PATTERN = "search-logs-*"; // 검색 로그 인덱스 패턴
    public static final String FIELD_QUERY = "query"; // 사용자가 입력한 검색어
    public static final String FIELD_QUERY_KEYWORD = "query.keyword"; // 검색어(키워드 형태, 집계용)
    public static final String FIELD_TIMESTAMP = "@timestamp"; // 로그가 기록된 시각


    //  집계(aggregation) 이름
    public static final String AGG_PER_HOUR = "per_hour";// 시간대별 집계
    public static final String AGG_POPULAR = "popular";// 인기 검색어 집계

    // 시간대
    public static final String TIME_ZONE_SEOUL = "Asia/Seoul"; // 한국 시간대 기준으로 집계

    //기본값
    public static final int DEFAULT_SUGGEST_SIZE = 10; // 자동완성 기본 개수

    // 검색 로그 인덱스 이름
    public static final String SEARCH_LOGS_INDEX = "search-logs-0001";// 실제 저장되는 검색 로그 인덱스
}
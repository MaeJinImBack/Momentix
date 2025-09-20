package com.example.momentix.domain.common.elasticsearch;

public final class IndexNames {
    private IndexNames() {}
    public static final String EVENTS = "events";

    // --- fields (ES mapping과 정확히 일치시켜야 함)
    public static final String FIELD_EVENT_TITLE = "eventTitle";
    public static final String FIELD_EVENT_TITLE_SUGGEST = "eventTitle_suggest";
    public static final String FIELD_PLACE_NAME = "placeName";
    public static final String FIELD_PLACE_NAME_SUGGEST = "placeName_suggest";
    public static final String FIELD_PLACE_ADDR = "placeAddress";
    public static final String FIELD_CATEGORY = "eventCategory";
    public static final String FIELD_START = "eventStartDate";
    public static final String FIELD_END = "eventEndDate";

    // --- search logs
    public static final String SEARCH_LOGS_PATTERN = "search-logs-*";
    public static final String FIELD_QUERY = "query";
    public static final String FIELD_QUERY_KEYWORD = "query.keyword";
    public static final String FIELD_TIMESTAMP = "@timestamp";

    // --- aggs
    public static final String AGG_PER_HOUR = "per_hour";
    public static final String AGG_POPULAR = "popular";

    public static final String TIME_ZONE_SEOUL = "Asia/Seoul";

    public static final int DEFAULT_SUGGEST_SIZE = 10;

    public static final String SEARCH_LOGS_INDEX = "search-logs-0001";
}
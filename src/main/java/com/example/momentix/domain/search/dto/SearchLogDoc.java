package com.example.momentix.domain.search.dto;

import com.example.momentix.domain.common.elasticsearch.IndexNames;
import com.fasterxml.jackson.annotation.JsonProperty;

// 엘라스틱서치에 저장할 검색로그
public class SearchLogDoc {
    // @JsonProperty로 엘라스틱 필드 이름을 정확히 맞춰줌
    @JsonProperty(IndexNames.FIELD_QUERY)
    private String query;// 사용자가 입력한 검색어

    @JsonProperty(IndexNames.FIELD_CATEGORY)
    private String category;// 카테고리

    @JsonProperty(IndexNames.FIELD_START)
    private String startDate;// 기간 시작(문자열로 저장)

    @JsonProperty(IndexNames.FIELD_END)
    private String endDate;// 기간 끝(문자열로 저장)

    private String userId;
    private String ip;

    @JsonProperty(IndexNames.FIELD_TIMESTAMP)
    private long timestamp; // 기록 시간

    public SearchLogDoc() {}

    public SearchLogDoc(String query, String category, String startDate, String endDate,
                        String userId, String ip, long  timestamp) {
        this.query = query;
        this.category = category;
        this.startDate = startDate;
        this.endDate = endDate;
        this.userId = userId;
        this.ip = ip;
        this.timestamp = timestamp;
    }

    // getters/setters
    public String getQuery() { return query; }
    public void setQuery(String query) { this.query = query; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getStartDate() { return startDate; }
    public void setStartDate(String startDate) { this.startDate = startDate; }
    public String getEndDate() { return endDate; }
    public void setEndDate(String endDate) { this.endDate = endDate; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getIp() { return ip; }
    public void setIp(String ip) { this.ip = ip; }
    public long  getTimestamp() { return timestamp; }
    public void setTimestamp(long  timestamp) { this.timestamp = timestamp; }
}

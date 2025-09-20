package com.example.momentix.domain.search.dto;


// 자동완성/인기검색어 공통으로 쓰는 응답 형태
public class AutocompleteResponse {
    private String suggestion;//추천 단어
    private long count;//해당 단어 몇 번 나왔는지(인기검색어용)

    public AutocompleteResponse() {}

    public AutocompleteResponse(String suggestion, long count) {
        this.suggestion = suggestion;
        this.count = count;
    }

    public String getSuggestion() { return suggestion; }
    public void setSuggestion(String suggestion) { this.suggestion = suggestion; }
    public long getCount() { return count; }
    public void setCount(long count) { this.count = count; }
}

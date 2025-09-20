package com.example.momentix.domain.search.dto;


//  "권지용" 검색하면 → suggestion = "권지용", count = 123
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

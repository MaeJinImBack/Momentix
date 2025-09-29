package com.example.momentix.domain.common.exception.search;


public enum SearchCode {

    EMPTY_SEARCH_KEYWORD(400, "검색어는 필수입니다."),
    INVALID_SEARCH_FILTER(400, "유효하지 않은 검색 필터입니다."),
    INVALID_SORT_OPTION(400, "유효하지 않은 정렬 옵션입니다."),
    SEARCH_SERVICE_UNAVAILABLE(503, "검색 서비스가 일시적으로 불안정합니다. 잠시 후 다시 시도해주세요.");

    private final int status;
    private final String message;

    SearchCode(int status, String message){
        this.status=status;
        this.message=message;
    }

    public int getStatus(){
        return status;
    }
    public String getMessage(){
        return message;
    }
}

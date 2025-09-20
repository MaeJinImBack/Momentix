package com.example.momentix.domain.search.repository;

import com.example.momentix.domain.search.dto.AutocompleteResponse;
import com.example.momentix.domain.search.dto.HourlyCountBucket;
import com.example.momentix.domain.search.dto.SearchLogDoc;

import java.util.List;


//집계 호출
public interface AnalyticsRepository {
    List<HourlyCountBucket> countPerHour(int hours) ;
    List<AutocompleteResponse> popularQueries(int hours, int size) ;

    //검색 로그
    void indexSearchLog(SearchLogDoc doc);
}
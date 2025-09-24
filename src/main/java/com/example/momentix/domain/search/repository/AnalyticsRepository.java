package com.example.momentix.domain.search.repository;

import com.example.momentix.domain.search.dto.AutocompleteResponse;
import com.example.momentix.domain.search.dto.HourlyCountBucket;
import com.example.momentix.domain.search.dto.SearchLogDoc;

import java.util.List;


//검색 로그를 바탕으로 한 통계/집계 기능
// 구현체가 실제로 엘라스틱서치에 요청을 날림
public interface AnalyticsRepository {

    // 최근 N 시간 동안 1시간 단위로 검색이 몇 건 있었는지
    List<HourlyCountBucket> countPerHour(int hours);

    // 최근 N 시간 동안 가장 많이 검색된 단어 
    List<AutocompleteResponse> popularQueries(int hours, int size);

    // 검색할 때 남기는 로그 한 건을 저장
    void indexSearchLog(SearchLogDoc doc);
}
package com.example.momentix.domain.search.impl;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.aggregations.Aggregate;
import co.elastic.clients.elasticsearch._types.aggregations.CalendarInterval;
import co.elastic.clients.elasticsearch._types.aggregations.DateHistogramBucket;
import co.elastic.clients.elasticsearch._types.aggregations.StringTermsBucket;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import com.example.momentix.domain.common.elasticsearch.IndexNames;
import com.example.momentix.domain.search.dto.AutocompleteResponse;
import com.example.momentix.domain.search.dto.HourlyCountBucket;
import com.example.momentix.domain.search.dto.SearchLogDoc;
import com.example.momentix.domain.search.repository.AnalyticsRepository;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

//집계 구현 (인기검색어/시간대)
@Repository
public class AnalyticsRepositoryImpl implements AnalyticsRepository {
    
    // 엘라스틱서치에 쌓인 검색 로그를 가지고 
    // 시간대별 검색 건수, 많이 검색된 단어(인기 검색어)
    // 를 뽑아오는 클래스
    
    private final ElasticsearchClient client;

    public AnalyticsRepositoryImpl(ElasticsearchClient client) {
        this.client = client;
    }

    @Override
    public List<HourlyCountBucket> countPerHour(int hours) {
        try {
            final int window = (hours <= 0) ? 24 : hours;

            SearchResponse<Void> res = client.search(s -> s
                            .index(IndexNames.SEARCH_LOGS_PATTERN)

                            .size(0)// 실제 문서는 안 가져오고(집계만 필요)

                            .query(q -> q.range(r -> r
                                    .date(d -> d
                                            .field(IndexNames.FIELD_TIMESTAMP)
                                            .gte("now-" + window + "h")
                                            .lte("now")
                                    )
                            ))

                            .aggregations(IndexNames.AGG_PER_HOUR, a -> a
                                    .dateHistogram(dh -> dh
                                            .field(IndexNames.FIELD_TIMESTAMP)// 시간 자를 기준
                                            .calendarInterval(CalendarInterval.Hour) // "1시간 단위"로 자르기
                                            .timeZone(IndexNames.TIME_ZONE_SEOUL)// 한국 시간 기준으로 자르기
                                            .minDocCount(0)// 빈 시간대도 0으로 채움
                                    )
                            ),
                    Void.class
            );


            //집계 결과 꺼내기. 결과가 없으면 빈 리스트
            Aggregate agg = res.aggregations().get(IndexNames.AGG_PER_HOUR);
            if (agg == null || agg.dateHistogram() == null)
                return List.of();


            List<HourlyCountBucket> out = new ArrayList<>();
            for (DateHistogramBucket b : agg.dateHistogram().buckets().array()) {
                long epochMillis = b.key();// 시간대 시작 시각
                LocalDateTime hour = LocalDateTime.ofInstant(
                        Instant.ofEpochMilli(epochMillis),
                        ZoneId.of(IndexNames.TIME_ZONE_SEOUL)
                );
                long count = b.docCount();// 그 시간대에 몇 건인지
                out.add(new HourlyCountBucket(hour, count));
            }
            return out;
        } catch (IOException e) {
            throw new RuntimeException("Elasticsearch countPerHour failed", e);
        }
    }


    @Override
    public List<AutocompleteResponse> popularQueries(int hours, int size) {
        // 기본 24시간, 상위 개수 기본 10개
        try {
            final int window = (hours <= 0) ? 24 : hours;
            final int topN = (size <= 0) ? 10 : size;

            SearchResponse<Void> res = client.search(s -> s
                            .index(IndexNames.SEARCH_LOGS_PATTERN)
                            .size(0)
                            .query(q -> q.range(r -> r
                                    .date(d -> d
                                            .field(IndexNames.FIELD_TIMESTAMP)
                                            .gte("now-" + window + "h")
                                            .lte("now")
                                    )
                            ))
                            .aggregations(IndexNames.AGG_POPULAR, a -> a
                                    .terms(t -> t
                                            .field(IndexNames.FIELD_QUERY_KEYWORD)
                                            .size(topN)
                                    )
                            ),
                    Void.class
            );


            //집계 결과 없으면 빈 리스트
            Aggregate agg = res.aggregations().get(IndexNames.AGG_POPULAR);
            if (agg == null || agg.sterms() == null)
                return List.of();


            //버킷마다 단어/건수 꺼내서 응답 형태로 변환
            List<AutocompleteResponse> out = new ArrayList<>();
            for (StringTermsBucket b : agg.sterms().buckets().array()) {
                String term = b.key().stringValue();// 검색어
                long count = b.docCount();// 횟수
                out.add(new AutocompleteResponse(term, count));
            }
            return out;
        } catch (IOException e) {
            throw new RuntimeException("Elasticsearch popularQueries failed", e);
        }
    }


    // 검색할 때마다 남기는 로그 한 건을 ES에 저장
    @Override
    public void indexSearchLog(SearchLogDoc doc) {
        try {
            client.index(i -> i
                    .index(IndexNames.SEARCH_LOGS_INDEX)
                    .document(doc)
            );
        } catch (IOException e) {
            throw new RuntimeException("Elasticsearch indexSearchLog failed", e);
        }
    }
}
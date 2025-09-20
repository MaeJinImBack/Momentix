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
                            .size(0)
                            .query(q -> q.range(r -> r
                                    .date(d -> d
                                            .field(IndexNames.FIELD_TIMESTAMP)
                                            .gte("now-" + window + "h")
                                            .lte("now")
                                    )
                            ))
                            .aggregations(IndexNames.AGG_PER_HOUR, a -> a
                                    .dateHistogram(dh -> dh
                                            .field(IndexNames.FIELD_TIMESTAMP)
                                            .calendarInterval(CalendarInterval.Hour) // ★ 여기!
                                            .timeZone(IndexNames.TIME_ZONE_SEOUL)
                                            .minDocCount(0)
                                    )
                            ),
                    Void.class
            );

            Aggregate agg = res.aggregations().get(IndexNames.AGG_PER_HOUR);
            if (agg == null || agg.dateHistogram() == null)
                return List.of();

            List<HourlyCountBucket> out = new ArrayList<>();
            for (DateHistogramBucket b : agg.dateHistogram().buckets().array()) {
                long epochMillis = b.key();
                LocalDateTime hour = LocalDateTime.ofInstant(
                        Instant.ofEpochMilli(epochMillis),
                        ZoneId.of(IndexNames.TIME_ZONE_SEOUL)
                );
                long count = b.docCount();
                out.add(new HourlyCountBucket(hour, count));
            }
            return out;
        } catch (IOException e) {
            throw new RuntimeException("Elasticsearch countPerHour failed", e);
        }
    }

    @Override
    public List<AutocompleteResponse> popularQueries(int hours, int size) {
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

            Aggregate agg = res.aggregations().get(IndexNames.AGG_POPULAR);
            if (agg == null || agg.sterms() == null)
                return List.of();

            List<AutocompleteResponse> out = new ArrayList<>();
            for (StringTermsBucket b : agg.sterms().buckets().array()) {
                String term = b.key().stringValue();
                long count = b.docCount();
                out.add(new AutocompleteResponse(term, count));
            }
            return out;
        } catch (IOException e) {
            throw new RuntimeException("Elasticsearch popularQueries failed", e);
        }
    }

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
package com.example.momentix.domain.search.bootstrap.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.BulkRequest;
import co.elastic.clients.elasticsearch.core.BulkResponse;
import co.elastic.clients.elasticsearch.core.bulk.BulkOperation;
import co.elastic.clients.elasticsearch.indices.CreateIndexRequest;
import co.elastic.clients.elasticsearch.indices.ExistsRequest;
import com.example.momentix.domain.common.elasticsearch.IndexNames;
import com.example.momentix.domain.events.entity.QEventPlace;
import com.example.momentix.domain.events.entity.QEvents;
import com.example.momentix.domain.events.entity.places.QPlaces;
import com.example.momentix.domain.search.bootstrap.dco.EventDoc;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

//인덱스 생성 + MySQL → ES 재색인 로직
@Service
public class EventIndexService {

    private static final Logger log = LoggerFactory.getLogger(EventIndexService.class);

    private final ElasticsearchClient elasticsearchClient; // ES 연결 클라이언트

    private final JPAQueryFactory queryFactory; // MySQL 조회용(QueryDSL)


    // 날짜를 "yyyy-MM-dd" 문자열로 맞춰서 넣기(ES date 매핑과 호환)
    private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("yyyy-MM-dd");


    public EventIndexService(ElasticsearchClient elasticsearchClient, JPAQueryFactory queryFactory) {
        this.elasticsearchClient = elasticsearchClient;
        this.queryFactory = queryFactory;
    }


    //인덱스 없으면 매핑과 함께 생성
    public void ensureEventsIndex() throws Exception {
        boolean exists = elasticsearchClient.indices().exists(ExistsRequest.of(b -> b.index(IndexNames.EVENTS))).value();
        if (exists) {
            log.info("[ES] '{}' 이미 존재합니다.", IndexNames.EVENTS);
            return;
        }

        CreateIndexRequest req = CreateIndexRequest.of(ci -> ci
                .index(IndexNames.EVENTS)
                .mappings(m -> m
                        // 카테고리: 정확히 일치 검색(=keyword)
                        .properties("eventCategory", p -> p.keyword(k -> k))

                        // 날짜
                        .properties("eventEndDate", p -> p.date(d -> d.format("yyyy-MM-dd||strict_date_optional_time||epoch_millis")))

                        // eventId: 텍스트지만 keyword 서브필드로도 보관
                        .properties("eventId", p -> p.text(t -> t.fields("keyword", f -> f.keyword(kb -> kb.ignoreAbove(256)))))

                        // 제목/장소이름
                        .properties("eventStartDate", p -> p.date(d -> d.format("yyyy-MM-dd||strict_date_optional_time||epoch_millis")))
                        .properties("eventTitle", p -> p.text(t -> t.fields("keyword", f -> f.keyword(k -> k))))
                        .properties("placeAddress", p -> p.text(t -> t))

                        // 주소
                        .properties("placeName", p -> p.text(t -> t.fields("keyword", f -> f.keyword(k -> k))))

                        // 자동완성용 필드(Completion 타입)
                        .properties("eventTitle_suggest", p -> p.completion(comp1 -> comp1
                                .analyzer("simple")
                                .preserveSeparators(true)
                                .preservePositionIncrements(true)
                                .maxInputLength(50)))
                        .properties("placeName_suggest", p -> p.completion(comp2 -> comp2
                                .analyzer("simple")
                                .preserveSeparators(true)
                                .preservePositionIncrements(true)
                                .maxInputLength(50)))
                )
        );

        elasticsearchClient.indices().create(req);
        log.info("[ES] 생성된 인덱스 '{}'", IndexNames.EVENTS);
    }


    //MySQL → ES로 전부 넣기
    public void reindexAllFromMySQL() throws Exception {
        QEvents e = QEvents.events;
        QEventPlace ep = QEventPlace.eventPlace;
        QPlaces p = QPlaces.places;

        //이벤트×장소 매핑이 있는 데이터만 뽑기
        List<Tuple> rows = queryFactory
                .select(
                        e.id, p.id, e.eventTitle, e.eventCategoryType,
                        e.eventStartDate, e.eventEndDate,
                        p.placeName, p.placeAddress
                )
                .from(e)
                .join(e.eventPlaceList, ep)
                .join(ep.places, p)
                .where(e.isDeleted.eq(false))
                .fetch();

        if (rows.isEmpty()) {
            log.info("[ES] 인덱스를 생성할 데이터가 없습니다.");
            return;
        }

        List<BulkOperation> ops = new ArrayList<>(rows.size());
        for (Tuple t : rows) {
            Long eventId = t.get(e.id);
            Long placeId = t.get(p.id);

            //ES 문서 ID를 "이벤트ID-장소ID"로 고정 → 중복 없이 덮어쓰기 쉬움
            String esId = eventId + "-" + placeId;

            String eventTitle = t.get(e.eventTitle);
            String eventCategory = t.get(e.eventCategoryType).name();
            String start = DF.format(t.get(e.eventStartDate));
            String end = DF.format(t.get(e.eventEndDate));
            String placeName = t.get(p.placeName);
            String placeAddress = t.get(p.placeAddress);

            // 자동완성
            EventDoc.SuggestField titleSg = new EventDoc.SuggestField(
                    List.of(eventTitle), null
            );
            EventDoc.SuggestField placeSg = new EventDoc.SuggestField(
                    List.of(placeName), null
            );

            // ES에 넣을 한 문서 만들기
            EventDoc doc = new EventDoc(
                    String.valueOf(eventId),
                    eventTitle,
                    eventCategory,
                    start,
                    end,
                    placeName,
                    placeAddress,
                    titleSg,
                    placeSg
            );

            ops.add(BulkOperation.of(b -> b.index(idx -> idx
                    .index(IndexNames.EVENTS)
                    .id(esId)
                    .document(doc))));
        }

        // 한 방에 전송
        BulkResponse resp = elasticsearchClient.bulk(BulkRequest.of(b -> b.operations(ops)));
        if (Boolean.TRUE.equals(resp.errors())) {
            log.warn("[ES] 대량 인덱싱에 오류: {}", resp);
        } else {
            log.info("[ES] {} 문서를 '{}'에 인덱싱했습니다.", ops.size(), IndexNames.EVENTS);
        }
    }
}

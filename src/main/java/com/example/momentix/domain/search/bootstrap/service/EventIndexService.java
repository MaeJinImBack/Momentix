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

    private final ElasticsearchClient es;
    private final JPAQueryFactory queryFactory;

    private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public EventIndexService(ElasticsearchClient es, JPAQueryFactory queryFactory) {
        this.es = es;
        this.queryFactory = queryFactory;
    }

    public void ensureEventsIndex() throws Exception {
        boolean exists = es.indices().exists(ExistsRequest.of(b -> b.index(IndexNames.EVENTS))).value();
        if (exists) {
            log.info("[ES] '{}' already exists", IndexNames.EVENTS);
            return;
        }

        CreateIndexRequest req = CreateIndexRequest.of(ci -> ci
                .index(IndexNames.EVENTS)
                .mappings(m -> m
                        .properties("eventCategory", p -> p.keyword(k -> k))
                        .properties("eventEndDate", p -> p.date(d -> d.format("yyyy-MM-dd||strict_date_optional_time||epoch_millis")))
                        .properties("eventId", p -> p.text(t -> t.fields("keyword", f -> f.keyword(kb -> kb.ignoreAbove(256)))))
                        .properties("eventStartDate", p -> p.date(d -> d.format("yyyy-MM-dd||strict_date_optional_time||epoch_millis")))
                        .properties("eventTitle", p -> p.text(t -> t.fields("keyword", f -> f.keyword(k -> k))))
                        .properties("placeAddress", p -> p.text(t -> t))
                        .properties("placeName", p -> p.text(t -> t.fields("keyword", f -> f.keyword(k -> k))))
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

        es.indices().create(req);
        log.info("[ES] Created index '{}'", IndexNames.EVENTS);
    }

    public void reindexAllFromMySQL() throws Exception {
        QEvents e = QEvents.events;
        QEventPlace ep = QEventPlace.eventPlace;
        QPlaces p = QPlaces.places;

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
            log.info("[ES] No data to index");
            return;
        }

        List<BulkOperation> ops = new ArrayList<>(rows.size());
        for (Tuple t : rows) {
            Long eventId = t.get(e.id);
            Long placeId = t.get(p.id);

            String esId = eventId + "-" + placeId;

            String eventTitle = t.get(e.eventTitle);
            String eventCategory = t.get(e.eventCategoryType).name();
            String start = DF.format(t.get(e.eventStartDate));
            String end   = DF.format(t.get(e.eventEndDate));
            String placeName = t.get(p.placeName);
            String placeAddress = t.get(p.placeAddress);

            EventDoc.SuggestField titleSg = new EventDoc.SuggestField(
                    List.of(eventTitle), null
            );
            EventDoc.SuggestField placeSg = new EventDoc.SuggestField(
                    List.of(placeName), null
            );

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

        BulkResponse resp = es.bulk(BulkRequest.of(b -> b.operations(ops)));
        if (Boolean.TRUE.equals(resp.errors())) {
            log.warn("[ES] Bulk indexing had errors: {}", resp);
        } else {
            log.info("[ES] Indexed {} docs into '{}'", ops.size(), IndexNames.EVENTS);
        }
    }
}

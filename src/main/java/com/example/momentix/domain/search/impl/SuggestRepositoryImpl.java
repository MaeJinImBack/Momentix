package com.example.momentix.domain.search.impl;


import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Suggestion;
import com.example.momentix.domain.common.elasticsearch.IndexNames;
import com.example.momentix.domain.search.dto.AutocompleteResponse;
import com.example.momentix.domain.search.repository.SuggestRepository;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.*;

//자동완성 구현  (completion 우선, 부족 시 prefix 보충)
//completion(자동완성 미리 넣어둔 사전)
//prefix(원본 텍스트에서 대로 시작하는 필드 직접 검색)
@Repository
public class SuggestRepositoryImpl implements SuggestRepository {

    // 1) 미리 준비된 "자동완성용 필드"(completion)에서 추천 먼저 가져오고
    // 2) 부족하면 실제 텍스트 필드에서 "앞부분 일치(prefix)"로 보충해서 채움

    private static final String SG_TITLE = "title-suggest";
    private static final String SG_PLACE = "place-suggest";

    private final ElasticsearchClient client;


    public SuggestRepositoryImpl(ElasticsearchClient client) {
        this.client = client;
    }

    private static String asString(Object o) {
        return (o == null) ? null : String.valueOf(o);
    }


    @Override
    public List<AutocompleteResponse> suggest(String query, int limit) {
        try {
            if (query == null || query.isBlank()) return List.of();


            final String queryText = query.trim();// 앞뒤 공백 제거
            final int size = Math.max(1, limit);// 최소 1개 이상은 요청


            // 1단계: completion suggester로 "제목"과 "장소"에서 자동완성 후보 뽑기
            // completion 필드는 인덱스 만들 때 따로 지정해둔 eventTitle_suggest / placeName_suggest
            // 철자 조금 틀려도 잡아주게 fuzziness = AUTO

            SearchResponse<Map<String, Object>> completionResponse = client.search(s -> s
                            .index(IndexNames.EVENTS)
                            .size(0)
                            .suggest(suggestBuilder -> suggestBuilder
                                    .suggesters(SG_TITLE, st -> st
                                            .prefix(queryText)
                                            .completion(c -> c
                                                    .field(IndexNames.FIELD_EVENT_TITLE_SUGGEST)
                                                    .size(size)
                                                    .fuzzy(f -> f.fuzziness("AUTO"))
                                            )
                                    )
                                    .suggesters(SG_PLACE, st -> st
                                            .prefix(queryText)
                                            .completion(c -> c
                                                    .field(IndexNames.FIELD_PLACE_NAME_SUGGEST)
                                                    .size(size)
                                                    .fuzzy(f -> f.fuzziness("AUTO"))
                                            )
                                    )
                            ),
                    (Class<Map<String, Object>>) (Class<?>) Map.class
            );

            //중복 제거용(입력값이 제목/장소 둘 다에서 나올 수 있으므로)
            LinkedHashSet<String> dedup = new LinkedHashSet<>();
            List<AutocompleteResponse> results = new ArrayList<>();

            // 결과에서 실제 텍스트만 뽑아서 담기
            Map<String, List<Suggestion<Map<String, Object>>>> suggestionsMap = completionResponse.suggest();
            if (suggestionsMap != null) {
                for (String key : new String[]{SG_TITLE, SG_PLACE}) {
                    List<Suggestion<Map<String, Object>>> suggestionList = suggestionsMap.get(key);
                    if (suggestionList == null || suggestionList.isEmpty()) continue;

                    suggestionList.get(0).completion().options().forEach(option -> {
                        String optionText = option.text();// 추천 텍스트
                        if (optionText != null && dedup.add(optionText)) {
                            results.add(new AutocompleteResponse(optionText, 0L));
                        }
                    });
                }
            }


            // 2단계: 추천 개수가 모자라면, 원문필드에서 앞부분 일치로 보충
            if (results.size() < size) {
                int remain = size - results.size();

                // eventTitle / placeName / placeAddress 세 필드에서
                // 앞 부분이 이 글자로 시작하는 문서 찾기
                SearchResponse<Map<String, Object>> prefixResponse = client.search(s -> s
                                .index(IndexNames.EVENTS)
                                .size(Math.max(remain, 1))
                                .query(qb -> qb.bool(b -> b
                                        .should(sh -> sh.matchPhrasePrefix(m -> m
                                                .field(IndexNames.FIELD_EVENT_TITLE)
                                                .query(queryText)
                                        ))
                                        .should(sh -> sh.matchPhrasePrefix(m -> m
                                                .field(IndexNames.FIELD_PLACE_NAME)
                                                .query(queryText)
                                        ))
                                        .should(sh -> sh.matchPhrasePrefix(m -> m
                                                .field(IndexNames.FIELD_PLACE_ADDR)
                                                .query(queryText)
                                        ))
                                )),
                        (Class<Map<String, Object>>) (Class<?>) Map.class
                );

                // 어떤 필드를 최우선으로 자동완성 후보로 쓸지 순서 정의
                List<String> candidateFields = List.of(
                        IndexNames.FIELD_EVENT_TITLE,
                        IndexNames.FIELD_PLACE_NAME,
                        IndexNames.FIELD_PLACE_ADDR
                );

                // 검색된 문서들에서 자동완성 후보 텍스트 고르는 규칙
                // 입력으로 시작하는 값이 있으면 그걸 채택
                // 없으면 포함하는 값 중 하나
                // 그래도 없으면 타이틀-> 장소-> 주소 순서로 첫 값
                // 이미 담은 텍스트는 또 안 담음

                prefixResponse.hits().hits().forEach(hit -> {
                    Map<String, Object> sourceMap = hit.source();
                    if (sourceMap == null) return;

                    String candidate = null;

                    // (1) 접두사 일치 우선
                    for (String field : candidateFields) {
                        String fieldValue = asString(sourceMap.get(field));
                        if (fieldValue == null) continue;
                        String normalized = fieldValue.trim();
                        if (!normalized.isBlank() && normalized.startsWith(queryText)) {
                            candidate = normalized;
                            break;
                        }
                    }

                    // (2) 포함 일치(보조)
                    if (candidate == null) {
                        for (String field : candidateFields) {
                            String fieldValue = asString(sourceMap.get(field));
                            if (fieldValue == null) continue;
                            String normalized = fieldValue.trim();
                            if (!normalized.isBlank() && normalized.contains(queryText)) {
                                candidate = normalized;
                                break;
                            }
                        }
                    }

                    // (3) 최종 폴백: 타이틀 → 장소 → 주소
                    if (candidate == null) {
                        candidate = Optional.ofNullable(asString(sourceMap.get(IndexNames.FIELD_EVENT_TITLE)))
                                .orElse(Optional.ofNullable(asString(sourceMap.get(IndexNames.FIELD_PLACE_NAME)))
                                        .orElse(asString(sourceMap.get(IndexNames.FIELD_PLACE_ADDR))));
                        if (candidate != null) candidate = candidate.trim();
                    }

                    // (4) 중복 없이 한 번만 추가
                    if (candidate != null && !candidate.isBlank() && dedup.add(candidate)) {
                        results.add(new AutocompleteResponse(candidate, 0L));
                    }
                });
            }

            // 요청 개수보다 많이 모였으면 앞에서 size개만 잘라서 반환
            return results.size() > size ? results.subList(0, size) : results;
        } catch (IOException e) {
            throw new RuntimeException("Elasticsearch suggest failed", e);
        }
    }
}
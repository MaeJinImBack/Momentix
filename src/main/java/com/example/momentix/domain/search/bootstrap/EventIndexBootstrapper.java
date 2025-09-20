package com.example.momentix.domain.search.bootstrap;


import com.example.momentix.domain.search.bootstrap.service.EventIndexService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


// 자동완성 엘라스틱 데브 툴에 매번 넣기 힘들어서 자동화만듦
// 앱 시작 시 발동
@Configuration
public class EventIndexBootstrapper  {
    private static final Logger log = LoggerFactory.getLogger(EventIndexBootstrapper.class);

    private final EventIndexService eventIndexService;

    public EventIndexBootstrapper(EventIndexService eventIndexService) {
        this.eventIndexService = eventIndexService;
    }

    @Bean
    public ApplicationRunner initEventIndex() {
        return args -> {
            try {
                // 1) 인덱스 없으면 생성
                eventIndexService.ensureEventsIndex();
                // 2) MySQL 데이터 전부 재색인
                eventIndexService.reindexAllFromMySQL();
                log.info("[ES] Bootstrap finished");
            } catch (Exception e) {
                log.error("[ES] Bootstrap failed", e);
            }
        };
    }
}

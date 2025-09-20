package com.example.momentix.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//// 엘라스틱서치와 연결 설정하는 곳
@Configuration
public class ElasticsearchConfig {
    @Value("${elasticsearch.url}")
    private String serverUrl;

    @Value("${elasticsearch.apikey}")
    private String apiKey;

    //스프링이 사용할 엘라스틱서치 클라이언트 만들기(Bean 등록
    @Bean
    public ElasticsearchClient elasticsearchClient() {
        // // HTTP로 엘라스틱서치에 붙는 기본 클라이언트
        RestClient restClient = RestClient.builder(HttpHost.create(serverUrl))
                //// 요청 보낼 때 항상 Authorization 헤더( ApiKey … ) 붙이기
                .setDefaultHeaders(new org.apache.http.Header[]{
                        new org.apache.http.message.BasicHeader("Authorization", "ApiKey " + apiKey)
                })
                .build();
        // 위 RestClient를 엘라스틱 자바 클라이언트가 쓰도록 감싸주기(전송/매퍼 설정)
        RestClientTransport transport = new RestClientTransport(restClient, new JacksonJsonpMapper());
        // 실제로 우리가 사용할 고수준 클라이언트
        return new ElasticsearchClient(transport);
    }
}

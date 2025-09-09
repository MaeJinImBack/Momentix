package com.example.momentix.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;


@Configuration
public class WebClientConfig {
    // WebClient: Spring에서 외부 API 서버랑 HTTP 통신할 때 쓰는 도구
    // Builder Bean: 공용 설정(baseUrl, header, timeout 등)을 한 번만 정의하고, 여러 서비스에서 주입받아 재사용 가능하게 만드는 것
    @Bean
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }
}

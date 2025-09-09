package com.example.momentix.domain.auth.util;


import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@RequiredArgsConstructor
public class OAuthClient {
    //외부 OAuth 서버(네이버, 카카오 등)에 HTTP 요청을 보내주기
    
    //Spring에서 빈으로 주입받음
    private final WebClient.Builder webClientBuilder;

    //GET 요청(프로필 조회용)-외부에서 URL과 accessToken을 받아옴
    public String get(String url, String accessToken){
        return webClientBuilder.build()//WebClient.Builder를 이용해 WebClient 객체를 생성
                .get()//HTTP GET 요청을 시작하겠다는 선언
                .uri(url)//요청을 보낼 대상 주소(URL) 지정
                .header(HttpHeaders.AUTHORIZATION, "Bearer "+accessToken)//Authorization: Bearer {토큰} 형식으로 HTTP요청헤더에 추가
                .retrieve()//응답(Response) 를 가져올 준비
                .bodyToMono(String.class)//응답 본문(body)을 String 타입으로 변환하겠다는 뜻
                .block();//원래 비동기인데, 결과 나올 때 까지 기다리겠다
    }
    
    //POST FORM요청 어세스토큰 발급용
    public String postForm(String url, String... formData){//"key1", "value1", "key2", "value2" 이런 식으로 짝지어 전달
        StringBuilder bodyBuilder = new StringBuilder();
        // 두칸씩 묶어서 key=value형태로 문자열 만듦
        for (int i = 0; i < formData.length; i += 2) {
            //&로 연결
            if (i > 0) bodyBuilder.append("&");
            bodyBuilder.append(formData[i]).append("=").append(formData[i + 1]);
        }
        //JSON을 String으로 반환
        return webClientBuilder.build()
                .post()
                .uri(url)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .bodyValue(bodyBuilder.toString())
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }
}

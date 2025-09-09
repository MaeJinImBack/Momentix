package com.example.momentix.domain.auth.service.oauth;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import com.example.momentix.domain.auth.entity.OAuthProvider;


//분기 처리
@Component
@RequiredArgsConstructor
public class OAuthServiceFactory {
    private final NaverOAuthService naverOAuthService;
    
    public OAuthService getOAuthService(OAuthProvider provider) {
        switch (provider){
            case  NAVER:
                return naverOAuthService;
//            case  KAKAO:
//                return KaKaoOAuthService;
            default:
                throw new IllegalArgumentException("지원하지 않는 프로바이더: "+ provider);
        }
    }
}

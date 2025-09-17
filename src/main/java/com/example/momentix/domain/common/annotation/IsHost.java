package com.example.momentix.domain.common.annotation;


import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
@PreAuthorize("hasRole('HOST')")
public @interface IsHost {
}

// 중괄호 안에 코드를 작성하는 방식이 아니라
// 위에 붙은 메타어노테이션들이 의미를 결정해줌

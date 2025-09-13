package com.example.momentix;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class MomentixApplication {

    public static void main(String[] args) {
        SpringApplication.run(MomentixApplication.class, args);
    }
}

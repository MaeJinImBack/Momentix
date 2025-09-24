package com.example.momentix.config;

import com.example.momentix.domain.queue.QueueConsumer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.stream.StreamMessageListenerContainer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.time.Duration;


@Configuration
public class RedisStreamConfig {
    @Bean
    public StreamMessageListenerContainer<String, MapRecord<String, String, String>> streamMessageListenerContainer(
            RedisConnectionFactory redisConnectionFactory,
            ThreadPoolTaskExecutor redisStreamTaskExecutor,
            QueueConsumer queueConsumer
    ){
        StreamMessageListenerContainer
                .StreamMessageListenerContainerOptions<String, MapRecord<String, String, String>> options =
                StreamMessageListenerContainer
                        .StreamMessageListenerContainerOptions.builder()
                        .pollTimeout(Duration.ofMillis(50))
                        .executor(redisStreamTaskExecutor)
                        .build();

        StreamMessageListenerContainer<String, MapRecord<String, String, String>> container =
                StreamMessageListenerContainer.create(redisConnectionFactory, options);

        return container;
    }

    @Bean
    public ThreadPoolTaskExecutor redisStreamTaskExecutor(){
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(4);
        executor.setMaxPoolSize(8);
        executor.setQueueCapacity(100);
        executor.initialize();
        return executor;

    }
}

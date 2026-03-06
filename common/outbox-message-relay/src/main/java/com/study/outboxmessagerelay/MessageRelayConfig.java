package com.study.outboxmessagerelay;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * 트랜잭션 종료 시, Event 전송 비동기로 처리
 */
@EnableAsync
@Configuration
// 작성되는 클래스들 bean으로 등록되도록
@ComponentScan("com.study.outboxmessagerelay")
// 10초마다 미전송 Event polling
@EnableScheduling
public class MessageRelayConfig {
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServer;


    @Bean
    public KafkaTemplate<String, String> messageRelayKafkaTemplate() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        // 이벤트 유실을 방지하기 위해 Transcational Outbox를 사용 -> All로 하여 다 받도록 함
        configProps.put(ProducerConfig.ACKS_CONFIG, "all");
        return new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(configProps));
    }

    /**
     * 비동기 전송을 위한 ThreadPool 설정
     * @return
     */
    @Bean
    public Executor messageRelayPublishEventExecutor(){
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(20);
        executor.setMaxPoolSize(50);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("mr-pub-event-");
        return executor;
    }

    /**
     * 미전송 Event, 10초마다 보내주기 위한 설정
     * 각 Application 마다 샤드가 조금씩 분할되어 할당
     * -> 싱글스레드로 미전송 Event 전송
     * @return
     */
    @Bean
    public Executor messageRelayPublishPendingEventExecutor(){
        return Executors.newSingleThreadScheduledExecutor();
    }
}

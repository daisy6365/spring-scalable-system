package com.study.hotarticle.consumer;

import com.study.event.Event;
import com.study.event.EventPayload;
import com.study.event.EventType;
import com.study.hotarticle.service.HotArticleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class HotArticleEventConsumer {
    private final HotArticleService hotArticleService;

    /**
     * 지정된 topic만 listener에 지정
     */
    @KafkaListener(topics = {
            EventType.Topic.SCALABLE_ARTICLE,
            EventType.Topic.SCALABLE_COMMENT,
            EventType.Topic.SCALABLE_LIKE,
            EventType.Topic.SCALABLE_VIEW
    })
    public void listen(String message, Acknowledgment ack) {
        log.info("[HotArticleEventConsumer.listen] received message = {}", message);
        // String Json 타입으로 들어온 message를 우리가 정의한 규격에 맞게 변경
        Event<EventPayload> event = Event.fromJson(message);
        if (event != null){
            hotArticleService.handleEvent(event);
        }
        // 해당 record가 처리 성공임을 kafka에 응답
        ack.acknowledge();
    }
}

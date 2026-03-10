package com.study.articleread.consumer;

import com.study.articleread.service.ArticleReadService;
import com.study.event.Event;
import com.study.event.EventPayload;
import com.study.event.EventType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ArticleReadEventConsumer {
    private final ArticleReadService articleReadService;

    @KafkaListener(topics = {
            EventType.Topic.SCALABLE_ARTICLE,
            EventType.Topic.SCALABLE_COMMENT,
            EventType.Topic.SCALABLE_LIKE,
    })
    public void listen(String message, Acknowledgment ack) {
        log.info("[ArticleReadEventConsumer.listen] message = {}", message);
        Event<EventPayload> event = Event.fromJson(message);
        if(event != null) {
            articleReadService.handleEvent(event);
        }
        ack.acknowledge();
    }
}

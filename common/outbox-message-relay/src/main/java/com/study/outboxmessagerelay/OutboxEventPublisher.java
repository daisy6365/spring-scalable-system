package com.study.outboxmessagerelay;

import com.study.event.Event;
import com.study.event.EventPayload;
import com.study.event.EventType;
import com.study.snowflake.Snowflake;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OutboxEventPublisher {
    private final Snowflake outboxIdSnowflake = new Snowflake();
    private final Snowflake eventIdSnowflake = new Snowflake();
    private final ApplicationEventPublisher applicationEventPublisher;

    /**
     * Article Service
     * 모듈을 의존성 받고
     * Event publisher를 통해 Event를 발행
     *
     * articleId = 10
     * shardKey == articleId랑 동일 할때
     * 물리적 샤드가 어딘지 알아채기 위해 % 4로 함
     * 10 % 4 = 2
     * 물리적 샤드의 위치 : 2
     */
    public void publish(EventType type, EventPayload payload, Long shardKey) {
        Outbox outbox = Outbox.create(
                outboxIdSnowflake.nextId(),
                type,
                Event.of(eventIdSnowflake.nextId(), type, payload).toJson(),
                shardKey % MessageRelayConstants.SHARD_COUNT
        );
        applicationEventPublisher.publishEvent(OutboxEvent.of(outbox));
    }

}

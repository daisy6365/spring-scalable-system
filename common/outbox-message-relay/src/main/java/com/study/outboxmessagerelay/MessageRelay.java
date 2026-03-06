package com.study.outboxmessagerelay;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class MessageRelay {
    private final OutboxRepository outboxRepository;
    // 살아있는 Application 확인 -> 할당된 샤드 반환
    private final MessageRelayCoordinator messageRelayCoordinator;
    // Event 전송 KafkaTemplate
    private final KafkaTemplate<String, String> messageRelayKafkaTemplate;

    /**
     * 하나의 Transaction에 대한 Event를 받을 수 있음
     * BEFORE_COMMIT: commit 전,
     * 데이터에 대한 비즈니스 로직 처리 이후 -> Transaction으로 단일하게 묶임
     */
    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void createOutbox(OutboxEvent outboxEvent){
        log.info("[MessageRelay.createOutbox] outboxEvent = {}", outboxEvent);
        outboxRepository.save(outboxEvent.getOutbox());
    }

    /**
     * AFTER_COMMIT : commit 후,
     * KafkaEvent를 발행
     * @Async : 별도의 비동기 Thread로 처리
     * -> 만들어둔 설정을 사용하도록 함
     * (messageRelayPublishEventExecutor)
     */
    @Async("messageRelayPublishEventExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void publishEvent(OutboxEvent outboxEvent){
        log.info("[MessageRelay.publishEvent]");
        publishEvent(outboxEvent.getOutbox());
    }

    private void publishEvent(Outbox outbox) {
        try {
            messageRelayKafkaTemplate.send(
                    outbox.getEventType().getTopic(),
                    // Kafka 전송 키
                    // -> 샤드 키
                    // -> 각 샤드키마다 동일한 Kafka Partition으로 전송
                    // -> 동일한 Partition 내에서는 순서 보장
                    String.valueOf(outbox.getShardKey()),
                    outbox.getPayload()
            ).get(1, TimeUnit.SECONDS);

            // 전송이 완료되었으므로 outbox에서 데이터 삭제
            outboxRepository.delete(outbox);
        } catch (Exception e) {
            log.error("[MessageRelay.publishEvent] outbox = {}", outbox, e);
        }
    }


    /**
     * 10초내로 미전송된 Event 들을 주기적으로 Polling하여 보내줌
     */
    @Scheduled(
            fixedDelay = 10, // 10초 마다
            initialDelay = 5, // 최초 5초는 딜레이
            timeUnit = TimeUnit.SECONDS, // 취급단위
            // 만들어둔 설정을 사용하도록 함
            // (messageRelayPublishEventExecutor)
            scheduler = "messageRelayPublishPendingEventExecutor"
    )
    public void publishPendingEvent() {
        // 현재 Application에 할당된 샤드 정보를 가져옴
        AssignedShard assignedShard = messageRelayCoordinator.assignedShards();
        log.info("[MessageRelay.publishPendingEvent] assignedShard size = {}", assignedShard.getShards().size());
        // 할당된 샤드에 대해 DB에 주기적으로 polling 하여
        // Kafka에 전송
        for (Long shard : assignedShard.getShards()) {
            // 미전송된 -> DB outbox에 남아있는 데이터들을 조회
            // 한번 조회할때마다 100개씩
            List<Outbox> outboxes = outboxRepository.findAllByShardKeyAndCreatedAtLessThanEqualOrderByCreatedAtAsc(
                    shard,
                    LocalDateTime.now().minusSeconds(10),
                    Pageable.ofSize(100)
            );
            // 조회된 데이터 전송
            for (Outbox outbox : outboxes) {
                publishEvent(outbox);
            }
        }
    }


}

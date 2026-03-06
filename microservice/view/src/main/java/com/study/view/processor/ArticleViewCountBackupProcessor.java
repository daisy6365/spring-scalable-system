package com.study.view.processor;

import com.study.event.EventType;
import com.study.event.payload.ArticleViewEventPayload;
import com.study.outboxmessagerelay.OutboxEventPublisher;
import com.study.view.entity.ArticleViewCount;
import com.study.view.repository.ArticleViewCountBackUpRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ArticleViewCountBackupProcessor {
    public final ArticleViewCountBackUpRepository articleViewCountBackUpRepository;
    private final OutboxEventPublisher outboxEventPublisher;

    @Transactional
    public void backUp(Long articleId, Long viewCount) {
        int result = articleViewCountBackUpRepository.updateViewCount(articleId, viewCount);
        if(result == 0){
            articleViewCountBackUpRepository.findById(articleId)
                    .ifPresentOrElse(ignored -> {}, () -> {
                        // 없으면 해당 데이터 적재
                        articleViewCountBackUpRepository.save(ArticleViewCount.init(articleId, viewCount));
                    });
        }

        outboxEventPublisher.publish(
                EventType.ARTICLE_VIEWED,
                ArticleViewEventPayload.builder()
                        .articleId(articleId)
                        .articleViewCount(viewCount)
                        .build(),
                articleId
        );
    }
}

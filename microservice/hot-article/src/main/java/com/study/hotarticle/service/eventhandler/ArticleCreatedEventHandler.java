package com.study.hotarticle.service.eventhandler;

import com.study.event.Event;
import com.study.event.EventType;
import com.study.event.payload.ArticleCreatedEventPayload;
import com.study.hotarticle.repository.ArticleCreatedTimeRepository;
import com.study.hotarticle.utils.TimeCalculatorUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ArticleCreatedEventHandler implements EventHandler<ArticleCreatedEventPayload> {
    private final ArticleCreatedTimeRepository articleCreatedTimeRepository;

    @Override
    public void handle(Event<ArticleCreatedEventPayload> event) {
        ArticleCreatedEventPayload payload = event.getPayload();
        // 생성시간 업데이트
        articleCreatedTimeRepository.createOrUpdate(
                payload.getArticleId(),
                payload.getCreateAt(),
                TimeCalculatorUtils.calculateDurationToMidnight()); // 자정까지만 저장해둠
    }

    @Override
    public boolean supports(Event<ArticleCreatedEventPayload> event) {
        return EventType.ARTICLE_CREATED == event.getType();
    }

    @Override
    public Long findArticleId(Event<ArticleCreatedEventPayload> event) {
        return event.getPayload().getArticleId();
    }
}

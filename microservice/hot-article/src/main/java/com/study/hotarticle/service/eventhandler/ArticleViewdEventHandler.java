package com.study.hotarticle.service.eventhandler;

import com.study.event.Event;
import com.study.event.EventType;
import com.study.event.payload.ArticleViewEventPayload;
import com.study.hotarticle.repository.ArticleViewCountRepository;
import com.study.hotarticle.utils.TimeCalculatorUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ArticleViewdEventHandler implements EventHandler<ArticleViewEventPayload> {
    private final ArticleViewCountRepository articleViewCountRepository;

    @Override
    public void handle(Event<ArticleViewEventPayload> event) {
        ArticleViewEventPayload payload = event.getPayload();
        articleViewCountRepository.createOrUpdate(
                payload.getArticleId(),
                payload.getArticleViewCount(),
                TimeCalculatorUtils.calculateDurationToMidnight()
        );
    }

    @Override
    public boolean supports(Event<ArticleViewEventPayload> event) {
        return EventType.ARTICLE_VIEWED == event.getType();
    }

    @Override
    public Long findArticleId(Event<ArticleViewEventPayload> event) {
        return event.getPayload().getArticleId();
    }
}

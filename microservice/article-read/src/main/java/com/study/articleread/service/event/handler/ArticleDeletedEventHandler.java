package com.study.articleread.service.event.handler;

import com.study.articleread.repository.ArticleIdListRepository;
import com.study.articleread.repository.ArticleQueryModelRepository;
import com.study.articleread.repository.BoardArticleCountRepository;
import com.study.event.Event;
import com.study.event.EventType;
import com.study.event.payload.ArticleDeletedEventPayload;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ArticleDeletedEventHandler implements EventHandler<ArticleDeletedEventPayload> {
    private final ArticleIdListRepository articleIdListRepository;
    private final ArticleQueryModelRepository articleQueryModelRepository;
    private final BoardArticleCountRepository boardArticleCountRepository;

    @Override
    public void handle(Event<ArticleDeletedEventPayload> event) {
        ArticleDeletedEventPayload payload = event.getPayload();
        articleIdListRepository.delete(payload.getBoardId(), payload.getArticleId());
        articleQueryModelRepository.delete(payload.getArticleId());
        boardArticleCountRepository.createOrUpdate(payload.getBoardId(), payload.getBoardArticleCount());
    }

    @Override
    public boolean supports(Event<ArticleDeletedEventPayload> event) {
        return EventType.ARTICLE_DELETED == event.getType();
    }
}

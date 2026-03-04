package com.study.hotarticle.service.eventhandler;

import com.study.event.Event;
import com.study.event.EventType;
import com.study.event.payload.ArticleDeletedEventPayload;
import com.study.hotarticle.repository.ArticleCreatedTimeRepository;
import com.study.hotarticle.repository.HotArticleListRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ArticleDeletedEventHandler implements EventHandler<ArticleDeletedEventPayload> {
    // 삭제이므로 인기글 목록 자체에서 지워져야함
    private final HotArticleListRepository hotArticleListRepository;
    private final ArticleCreatedTimeRepository articleCreatedTimeRepository;

    @Override
    public void handle(Event<ArticleDeletedEventPayload> event) {
        ArticleDeletedEventPayload payload = event.getPayload();
        // 게시글 생성일자에서 삭제
        articleCreatedTimeRepository.delete(payload.getArticleId());
        // 인기글 목록에서 삭제
        hotArticleListRepository.remove(payload.getArticleId(), payload.getCreateAt());
    }

    @Override
    public boolean supports(Event<ArticleDeletedEventPayload> event) {
        return EventType.ARTICLE_DELETED == event.getType();
    }

    @Override
    public Long findArticleId(Event<ArticleDeletedEventPayload> event) {
        return event.getPayload().getArticleId();
    }
}

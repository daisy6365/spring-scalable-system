package com.study.articleread.service.event.handler;

import com.study.articleread.repository.ArticleQueryModelRepository;
import com.study.event.Event;
import com.study.event.EventType;
import com.study.event.payload.CommentDeletedEventPayload;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommentDeletedEventHandler implements EventHandler<CommentDeletedEventPayload> {
    private final ArticleQueryModelRepository articleQueryModelRepository;

    @Override
    public void handle(Event<CommentDeletedEventPayload> event) {
        articleQueryModelRepository.read(event.getPayload().getArticleId())
                .ifPresent(articleQueryModel -> {
                    articleQueryModel.updateBy(event.getPayload());
                    articleQueryModelRepository.update(articleQueryModel);
                });
    }

    @Override
    public boolean supports(Event<CommentDeletedEventPayload> event) {
        return EventType.COMMENT_DELETED == event.getType();
    }
}

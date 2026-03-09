package com.study.articleread.service;

import com.study.articleread.client.ArticleClient;
import com.study.articleread.client.CommentClient;
import com.study.articleread.client.LikeClient;
import com.study.articleread.client.ViewClient;
import com.study.articleread.repository.ArticleQueryModel;
import com.study.articleread.repository.ArticleQueryModelRepository;
import com.study.articleread.service.event.handler.EventHandler;
import com.study.articleread.service.response.ArticleReadResponse;
import com.study.event.Event;
import com.study.event.EventPayload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ArticleReadService {
    private final ArticleClient articleClient;
    private final CommentClient commentClient;
    private final LikeClient likeClient;
    private final ViewClient viewClient;
    private final ArticleQueryModelRepository articleQueryModelRepository;
    private final List<EventHandler> eventHandlers;

    public void handleEvent(Event<EventPayload> event) {
        for (EventHandler handler : eventHandlers) {
            if(handler.supports(event)) {
                // 지원하는 핸들러를 찾음
                handler.handle(event);
            }
        }
    }

    public ArticleReadResponse read(Long articleId){
        ArticleQueryModel articleQueryModel = articleQueryModelRepository.read(articleId)
                .or(() -> fetch(articleId))
                .orElseThrow();

        return ArticleReadResponse.from(
                articleQueryModel,
                viewClient.count(articleId)
        );
    }

    private Optional<ArticleQueryModel> fetch(Long articleId){
        Optional<ArticleQueryModel> articleQueryModelOptional = articleClient.read(articleId)
                .map(article -> ArticleQueryModel.create(
                        article,
                        commentClient.count(articleId),
                        likeClient.count(articleId)
                ));

        articleQueryModelOptional
                .ifPresent(articleQueryModel -> articleQueryModelRepository.create(articleQueryModel, Duration.ofDays(1)));
        log.info("[ArticleReadService.fetch] fetch data. articleId = {}, isPresent = {}", articleId, articleQueryModelOptional.isPresent());
        return articleQueryModelOptional;
    }
}

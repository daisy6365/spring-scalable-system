package com.study.articleread.service;

import com.study.articleread.client.ArticleClient;
import com.study.articleread.client.CommentClient;
import com.study.articleread.client.LikeClient;
import com.study.articleread.client.ViewClient;
import com.study.articleread.repository.ArticleIdListRepository;
import com.study.articleread.repository.ArticleQueryModel;
import com.study.articleread.repository.ArticleQueryModelRepository;
import com.study.articleread.repository.BoardArticleCountRepository;
import com.study.articleread.service.event.handler.EventHandler;
import com.study.articleread.service.response.ArticleReadPageResponse;
import com.study.articleread.service.response.ArticleReadResponse;
import com.study.event.Event;
import com.study.event.EventPayload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
    private final ArticleIdListRepository articleIdListRepository;
    private final BoardArticleCountRepository boardArticleCountRepository;
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

    public ArticleReadPageResponse readAll(Long boardId, Long page, Long pageSize){
        return ArticleReadPageResponse.of(
                readAll(readAllArticles(boardId, page, pageSize)),
                count(boardId)
        );
    }

    private List<ArticleReadResponse> readAll(List<Long> articleIds){
        Map<Long, ArticleQueryModel> articleQueryModelMap = articleQueryModelRepository.readAll(articleIds);
        return articleIds.stream()
                .map(articleId -> articleQueryModelMap.containsKey(articleId) ?
                        articleQueryModelMap.get(articleId) :
                        fetch(articleId).orElse(null))
                .filter(Objects::nonNull)
                .map(articleQueryModel ->
                        ArticleReadResponse.from(articleQueryModel, viewClient.count(articleQueryModel.getArticleId())))
                .toList();
    }

    private List<Long> readAllArticles(Long boardId, Long page, Long pageSize){
        List<Long> articleIds = articleIdListRepository.readAll(boardId, (page - 1) * pageSize, pageSize);
        if(pageSize == articleIds.size()){
            // 해당 page에 대한 목록이 redis에 다 저장되어있음
            log.info("[ArticleReadService.readAllArticles] return redis data.");
            return articleIds;
        }
        // redis에 없음 -> 원본 데이터를 가져와야 함
        log.info("[ArticleReadService.readAllArticles] return origin data.");
        return articleClient.readAll(boardId, page, pageSize).getArticles().stream()
                .map(ArticleClient.ArticleResponse::getArticleId)
                .toList();

    }

    private Long count(Long boardId) {
        Long result = boardArticleCountRepository.read(boardId);
        if (result != null) {
            return result;
        }
        long count = articleClient.count(boardId);
        boardArticleCountRepository.createOrUpdate(boardId, count);
        return count;
    }

    public List<ArticleReadResponse> readAllInfiniteScroll(Long boardId, Long lastArticleId, Long pageSize){
        return readAll(
                readAllInfiniteScrollArticleIds(boardId, lastArticleId, pageSize)
        );
    }

    private List<Long> readAllInfiniteScrollArticleIds(Long boardId, Long lastArticleId, Long pageSize) {
        List<Long> articleIds = articleIdListRepository.readAllInfiniteScroll(boardId, lastArticleId, pageSize);
        if(pageSize == articleIds.size()){
            // 해당 page에 대한 목록이 redis에 다 저장되어있음
            log.info("[ArticleReadService.readAllInfiniteScrollArticleIds] return redis data.");
            return articleIds;
        }
        // redis에 없음 -> 원본 데이터를 가져와야 함
        log.info("[ArticleReadService.readAllInfiniteScrollArticleIds] return origin data.");
        return articleClient.readAllInfiniteScroll(boardId, lastArticleId, pageSize).stream()
                .map(ArticleClient.ArticleResponse::getArticleId)
                .toList();
    }
}

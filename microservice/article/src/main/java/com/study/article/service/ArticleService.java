package com.study.article.service;

import com.study.article.entity.Article;
import com.study.article.entity.BoardArticleCount;
import com.study.article.repository.ArticleRepository;
import com.study.article.repository.BoardArticleCountRepository;
import com.study.article.request.ArticleCreateRequest;
import com.study.article.request.ArticleUpdateRequest;
import com.study.article.response.ArticlePageResponse;
import com.study.article.response.ArticleResponse;
import com.study.article.util.PageLimitCalculator;
import com.study.event.EventType;
import com.study.event.payload.ArticleCreatedEventPayload;
import com.study.event.payload.ArticleDeletedEventPayload;
import com.study.event.payload.ArticleUpdatedEventPayload;
import com.study.outboxmessagerelay.OutboxEventPublisher;
import com.study.snowflake.Snowflake;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ArticleService {
    // 동일 인스턴스에서 상태 유지
    private final Snowflake snowflake = new Snowflake();
    private final ArticleRepository articleRepository;
    private final BoardArticleCountRepository boardArticleCountRepository;
    private final OutboxEventPublisher outboxEventPublisher;

    @Transactional
    public ArticleResponse create(ArticleCreateRequest request){
        Article article = articleRepository.save(
                Article.create(snowflake.nextId(), request.getTitle(), request.getContent(), request.getBoardId(), request.getWriterId())
        );
        // 게시판의 글 갯수 카운트
        int result = boardArticleCountRepository.increase(request.getBoardId());
        if(result == 0) {
            boardArticleCountRepository.save(
                    BoardArticleCount.create(request.getBoardId(), 1L)
            );
        }

        /**
         * article.getBoardId() : 단일 Transaction에서 동일한 샤드로 처리되어야함
         * -> 동일한 board로 묶음
         */
        outboxEventPublisher.publish(
                EventType.ARTICLE_CREATED,
                ArticleCreatedEventPayload.builder()
                        .articleId(article.getArticleId())
                        .title(article.getTitle())
                        .content(article.getContent())
                        .boardId(article.getBoardId())
                        .writerId(article.getWriterId())
                        .createAt(article.getCreatedAt())
                        .modifiedAt(article.getUpdatedAt())
                        .boardArticleCount(count(article.getBoardId()))
                        .build(),
                article.getBoardId()
        );
        return ArticleResponse.from(article);
    }

    @Transactional
    public ArticleResponse update(Long articleId, ArticleUpdateRequest request){
        Article article = articleRepository.findById(articleId).orElseThrow();

        article.update(request.getTitle(), request.getContent());

        outboxEventPublisher.publish(
                EventType.ARTICLE_UPDATED,
                ArticleUpdatedEventPayload.builder()
                        .articleId(article.getArticleId())
                        .title(article.getTitle())
                        .content(article.getContent())
                        .boardId(article.getBoardId())
                        .writerId(article.getWriterId())
                        .createAt(article.getCreatedAt())
                        .modifiedAt(article.getUpdatedAt())
                        .build(),
                article.getBoardId()
        );
        return ArticleResponse.from(article);
    }

    public ArticleResponse read(Long articleId){
        return ArticleResponse.from(articleRepository.findById(articleId).orElseThrow());
    }

    @Transactional
    public void delete(Long articleId){
//        articleRepository.deleteById(articleId);
        Article article = articleRepository.findById(articleId).orElseThrow();
        articleRepository.delete(article);
        boardArticleCountRepository.decrease(article.getBoardId());

        outboxEventPublisher.publish(
                EventType.ARTICLE_DELETED,
                ArticleDeletedEventPayload.builder()
                        .articleId(article.getArticleId())
                        .title(article.getTitle())
                        .content(article.getContent())
                        .boardId(article.getBoardId())
                        .writerId(article.getWriterId())
                        .createAt(article.getCreatedAt())
                        .modifiedAt(article.getUpdatedAt())
                        .build(),
                article.getBoardId()
        );
    }

    public ArticlePageResponse readAll(Long boardId, Long page, Long pageSize){
        List<ArticleResponse> articleResponses = articleRepository.findAll(boardId, (page - 1) * pageSize, pageSize).stream()
                .map(ArticleResponse::from)
                .toList();
        // 현재 응답 페이징의 갯수
        Long count = articleRepository.count(boardId, PageLimitCalculator.calculatePageLimit(page, pageSize, 10L));

        return ArticlePageResponse.from(articleResponses, count);
    }

    /**
     * 무한 스크롤 설계
     * @param lastArticleId : 현재 어디까지 봤는지 나타내는 커서
     * @return
     */
    public List<ArticleResponse> readAllInfiniteScroll(Long boardId, Long pageSize, Long lastArticleId){
        List<Article> articles = lastArticleId == null ?
                // 처음 조회
                articleRepository.findAllInfiniteScroll(boardId, pageSize) :
                // 현재 본 게시글 다음부터 조회
                articleRepository.findAllInfiniteScroll(boardId, pageSize, lastArticleId);

        return articles.stream()
                .map(ArticleResponse::from)
                .toList();
    }

    public Long count(Long boardId){
        return boardArticleCountRepository.findById(boardId)
                .map(BoardArticleCount::getArticleCount)
                .orElse(0L);
    }
}
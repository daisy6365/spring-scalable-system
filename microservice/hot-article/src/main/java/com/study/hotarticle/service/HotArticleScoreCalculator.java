package com.study.hotarticle.service;

import com.study.hotarticle.repository.ArticleCommentCountRepository;
import com.study.hotarticle.repository.ArticleLikeCountRepository;
import com.study.hotarticle.repository.ArticleViewCountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 인기글에 대한 점수 계산
 */
@Component
@RequiredArgsConstructor
public class HotArticleScoreCalculator {
    private final ArticleCommentCountRepository articleCommentCountRepository;
    private final ArticleLikeCountRepository articleLikeCountRepository;
    private final ArticleViewCountRepository articleViewCountRepository;

    // 점수 계산을 위한 기준 가중치 -> 상수화
    private static final long ARTICLE_COMMENT_COUNT_WEIGHT = 2;
    private static final long ARTICLE_LIKE_COUNT_WEIGHT = 3;
    private static final long ARTICLE_VIEW_COUNT_WEIGHT = 1;

    public long calculate(Long articleId) {
        Long articleCommentCount = articleCommentCountRepository.read(articleId);
        Long articleLikeCount = articleLikeCountRepository.read(articleId);
        Long articleViewCount = articleViewCountRepository.read(articleId);
        return articleCommentCount * ARTICLE_COMMENT_COUNT_WEIGHT
                + articleLikeCount * ARTICLE_LIKE_COUNT_WEIGHT
                + articleViewCount * ARTICLE_VIEW_COUNT_WEIGHT;
    }
}

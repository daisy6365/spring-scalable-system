package com.study.view.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Redis를 사용하나, 백업용으로 테이블 생성
 */
@Entity
@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ArticleViewCount {
    @Id
    private Long articleId;
    private Long viewCount;

    public static ArticleViewCount init(Long articleId, Long viewCount) {
        ArticleViewCount articleViewCount = new ArticleViewCount();
        articleViewCount.articleId = articleId;
        articleViewCount.viewCount = viewCount;
        return articleViewCount;
    }
}

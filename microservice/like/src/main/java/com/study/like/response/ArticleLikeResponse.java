package com.study.like.response;

import com.study.like.entity.ArticleLike;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@ToString
public class ArticleLikeResponse {
    private Long articleLikeId;
    private Long articleId;
    private Long userId;
    private LocalDateTime createAt;

    public static ArticleLikeResponse from(ArticleLike articleLike){
        ArticleLikeResponse articleLikeResponse = new ArticleLikeResponse();
        articleLikeResponse.articleLikeId = articleLike.getArticleLikeId();
        articleLikeResponse.articleId = articleLike.getArticleId();
        articleLikeResponse.userId = articleLike.getUserId();
        articleLikeResponse.createAt = articleLike.getCreateAt();
        return articleLikeResponse;
    }
}

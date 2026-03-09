package com.study.articleread.repository;

import com.study.articleread.client.ArticleClient;
import com.study.event.payload.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ArticleQueryModel  {
    private Long articleId;
    private String title;
    private String content;
    private Long boardId;
    private Long writerId;
    private LocalDateTime createAt;
    private LocalDateTime modifiedAt;
    private Long articleCommentCount;
    private Long articleLikeCount;

    /**
     * Kafka로 부터 받은 데이터
     */
    public static ArticleQueryModel create(ArticleCreatedEventPayload payload) {
        ArticleQueryModel model = new ArticleQueryModel();
        model.articleId = payload.getArticleId();
        model.title = payload.getTitle();
        model.content = payload.getContent();
        model.boardId = payload.getBoardId();
        model.writerId = payload.getWriterId();
        model.createAt = payload.getCreateAt();
        model.modifiedAt = payload.getModifiedAt();

        // 처음 생성 됐을때에는, 댓글/좋아요/조회수 모두 0
        model.articleCommentCount = 0L;
        model.articleLikeCount = 0L;
        return model;
    }

    /**
     * 원본데이터가 없을 때, API Client를 통해 받은 데이터
     */
    public static ArticleQueryModel create(ArticleClient.ArticleResponse article, Long commentCount, Long likeCount) {
        ArticleQueryModel model = new ArticleQueryModel();
        model.articleId = article.getArticleId();
        model.title = article.getTitle();
        model.content = article.getContent();
        model.boardId = article.getBoardId();
        model.writerId = article.getWriterId();
        model.createAt = article.getCreatedAt();
        model.modifiedAt = article.getModifiedAt();
        model.articleCommentCount = commentCount;
        model.articleLikeCount = likeCount;
        return model;
    }

    /**
     * 게시글 생성 외에도,
     * 게시글/댓글/좋아요에 대한 수정, 삭제 Event에 따라
     * ArticleQueryModel에도 데이터가 반영되어야 함
     */
    public void updateBy(ArticleUpdatedEventPayload payload) {
        this.title = payload.getTitle();
        this.content = payload.getContent();
        this.boardId = payload.getBoardId();
        this.writerId = payload.getWriterId();
        this.createAt = payload.getCreateAt();
        this.modifiedAt = payload.getModifiedAt();
    }

    public void updateBy(CommentCreatedEventPayload payload) {
        this.articleCommentCount = payload.getArticleCommentCount();
    }

    public void updateBy(CommentDeletedEventPayload payload) {
        this.articleCommentCount = payload.getArticleCommentCount();
    }

    public void updateBy(ArticleLikedEventPayload payload) {
        this.articleLikeCount = payload.getArticleLikeCount();
    }

    public void updateBy(ArticleUnlikedEventPayload payload) {
        this.articleLikeCount = payload.getArticleLikeCount();
    }
}

package com.study.article.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Article {
    @Id
    private Long articleId;
    private String title;
    private String content;
    /**
     * Shard Key
     * 특정 단위로 나누어 데이터베이스에 분산 저장
     * -> 특정 단위로 나뉘는 기준 : 가장 많이 조회되는 패턴을 고려
     * articleId -> 게시글마다 샤드 분산 저장
     * boardId -> 게시판 종류별로 샤드 분산 저장
     */
    private Long boardId;
    private Long writerId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static Article create(Long articleId, String title, String content, Long boardId, Long writerId){
        Article article = new Article();
        article.articleId = articleId;
        article.title = title;
        article.content = content;
        article.boardId = boardId;
        article.writerId = writerId;
        article.createdAt = LocalDateTime.now();
        article.updatedAt = article.createdAt;

        return article;
    }

    public void update(String title, String content){
        this.title = title;
        this.content = content;
        this.updatedAt = LocalDateTime.now();
    }
}

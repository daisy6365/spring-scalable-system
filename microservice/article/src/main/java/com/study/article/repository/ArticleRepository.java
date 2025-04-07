package com.study.article.repository;

import com.study.article.entity.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {
    @Query(value = """
        SELECT article.article_id, article.title, article.content, article.board_id, article.writer_id, article.created_at, article.updated_at
        FROM (
            SELECT article_id
            FROM article
            WHERE board_id = :boardId
            ORDER BY article_id DESC 
            LIMIT :limit
            OFFSET :offset
        ) T
        LEFT JOIN article
        ON T.article_id = article.article_id """, nativeQuery = true)
    List<Article> findAll(@Param("boardId") Long boardId,
                          @Param("offset") Long offset,
                          @Param("limit") Long limit);

    @Query(value = """
        SELECT COUNT(*)
        FROM (
            SELECT article_id
            FROM article
            WHERE board_id = :boardId
            LIMIT :limit
        ) T """, nativeQuery = true)
    Long count(@Param("boardId") Long boardId,
               @Param("limit") Long limit);
}

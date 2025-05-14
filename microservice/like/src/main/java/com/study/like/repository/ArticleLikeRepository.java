package com.study.like.repository;

import com.study.like.entity.ArticleLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ArticleLikeRepository extends JpaRepository<ArticleLike, Long> {
    Optional<ArticleLike> findByArticleIdAndUserId(Long articleId, Long userId);
    @Query(value = """
        DELETE FROM article_like
        WHERE article_id = :articleId
    """,nativeQuery = true)
    int delete(Long articleId);
}

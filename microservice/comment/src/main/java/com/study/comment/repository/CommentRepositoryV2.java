package com.study.comment.repository;

import com.study.comment.entity.Comment;
import com.study.comment.entity.CommentV2;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CommentRepositoryV2 extends JpaRepository<CommentV2, Long> {
    @Query("select c from CommentV2 c where c.commentPath.path = :path")
    Optional<CommentV2> findByPath(@Param("path") String path);

    @Query(value = """
        SELECT path
        FROM  comment_v2
        WHERE article_id = :articleId
        AND path > :pathPrefix
        AND path LIKE :pathPrefix%
        ORDER BY path DESC
        LIMIT 1
    """, nativeQuery = true)
    Optional<String> findDescendantsTopPath(@Param("articleId")Long articleId,
                                            @Param("pathPrefix")String pathPrefix);

    // 페이징 쿼리
    @Query(value = """
        SELECT comment_v2.comment_id, comment_v2.content, comment_v2.path, comment_v2.article_id,
               comment_v2.writer_id, comment_v2.deleted, comment_v2.created_at
        FROM (
            SELECT comment_id
            FROM comment_v2
            WHERE article_id = :articleId
            ORDER BY path ASC
            LIMIT :limit
            OFFSET :offset
        ) t
        LEFT JOIN comment_v2 
        ON t.comment_id = comment_v2.comment_id
    """, nativeQuery = true)
    List<CommentV2> findAll(@Param("articleId") Long articleId,
                            @Param("offset") Long offset,
                            @Param("limit")Long limit);


    @Query(value = """
        SELECT COUNT(*)
        FROM (
            SELECT comment_id
            FROM comment_v2
            WHERE article_id = :articleId
            LIMIT :limit
        ) t
    """, nativeQuery = true)
    Long countByArticleId(@Param("articleId")Long articleId,
                          @Param("limit")Long limit);


    @Query(value = """
        SELECT *
        FROM comment_v2
        WHERE article_id = :articleId
        ORDER BY path ASC
        LIMIT :limit
    """, nativeQuery = true)
    List<CommentV2> findAllInifiteScroll(@Param("articleId")Long articleId,
                                          @Param("limit")Long limit);

    // 이후 무한 스크롤 페이징
    @Query(value = """
        SELECT *
        FROM comment_v2
        WHERE article_id = :articleId
        AND path > :lastPath
        ORDER BY path ASC
        LIMIT :limit
    """, nativeQuery = true)
    List<CommentV2> findAllInifiteScroll(@Param("articleId") Long articleId,
                                       @Param("lastPath") String lastPath,
                                       @Param("limit") Long limit);
}

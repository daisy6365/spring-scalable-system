package com.study.comment.repository;

import com.study.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    // covering Index 사용 -> comment_id만 가져옴
    @Query(value = """
        SELECT COUNT(*)
        FROM (
            SELECT comment_id
            FROM comment
            WHERE article_id = :articleId
            AND parent_comment_id = :parentCommentId
            LIMIT :limit
        ) T
    """, nativeQuery = true)
    Long countByParentCommentId(@Param("articleId") Long articleId,
                                @Param("parentCommentId") Long parentCommentId,
                                @Param("limit") Long limit);
}

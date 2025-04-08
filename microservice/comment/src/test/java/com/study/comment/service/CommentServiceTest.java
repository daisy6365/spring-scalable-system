package com.study.comment.service;

import com.study.comment.entity.Comment;
import com.study.comment.repository.CommentRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

// 단위 테스트
@ExtendWith(MockitoExtension.class)
class CommentServiceTest {
    @InjectMocks
    CommentService commentService;
    @Mock
    CommentRepository commentRepository;

    @Test
    @DisplayName("삭제할 댓글을 기준으로, 자식댓글이 존재할 시, 삭제표시만 한다.")
    void deleteShoudMarkDeltedIfHasChildren(){
        // given
        Long articleId = 1L;
        Long commentId = 2L;
        Comment comment = createComment(articleId, commentId);
        given(commentRepository.findById(commentId)).willReturn(Optional.of(comment));
        given(commentRepository.countByParentCommentId(articleId, commentId, 2L)).willReturn(2L);

        // when
        commentService.delete(commentId);

        // then
        verify(comment).delete();
    }

    private Comment createComment(Long articleId, Long commentId) {
        Comment comment = mock(Comment.class);
        given(comment.getArticleId()).willReturn(articleId);
        given(comment.getCommentId()).willReturn(commentId);

        return comment;
    }


    private Comment createComment(Long articleId, Long commentId, Long parentCommentId) {
        Comment comment = createComment(articleId, commentId);
        given(comment.getParentCommentId()).willReturn(parentCommentId);

        return comment;
    }
}
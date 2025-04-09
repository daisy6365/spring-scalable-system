package com.study.comment.service;

import com.study.comment.entity.Comment;
import com.study.comment.repository.CommentRepository;
import com.study.comment.request.CommentCreateRequest;
import com.study.comment.response.CommentResponse;
import com.study.snowflake.Snowflake;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.function.Predicate;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final Snowflake snowflake = new Snowflake();
    private final CommentRepository commentRepository;

    @Transactional
    public CommentResponse create(CommentCreateRequest request){
        Comment parent = findParent(request);
        Comment comment = commentRepository.save(Comment.create(snowflake.nextId(),
                request.getContent(),
                parent == null ? null : parent.getCommentId(),
                request.getArticleId(),
                request.getWriterId()));

        return CommentResponse.from(comment);
    }

    public CommentResponse read(Long commentId){
        Comment comment = commentRepository.findById(commentId).orElse(null);
        return CommentResponse.from(comment);
    }

    @Transactional
    public void delete(Long commentId){
        commentRepository.findById(commentId)
                .filter(Predicate.not(Comment::getDeleted))
                .ifPresent(comment -> {
                    if(hasChildren(comment)){
                        // 하위 댓글이 존재할 시, 삭제여부 상태값만 바꿈
                        comment.delete();
                    }
                    else{
                        // 하위 댓글이 존재하지 않을 시, 데이터 자체를 삭제
                        delete(comment);
                    }
                });
    }


    private Comment findParent(CommentCreateRequest request){
        if(request.getParentCommentId() == null){
            return null;
        }
        return commentRepository.findById(request.getParentCommentId())
                // 아직 삭제 되지 않은 부모 댓글이어야 함
                .filter(Predicate.not(Comment::getDeleted))
                // 최대 2 DEPTH 이기 때문에 부모는 루트 댓글이어야 함
                .filter(Comment::isRoot)
                .orElse(null);
    }

    private boolean hasChildren(Comment comment){
        // 삭제하려는 댓글 ID를 대상으로 한 부모 댓글 아이디가 존재할 때.
        // 자기자신 포함 2개 존재 -> 하위 댓글 있는 것으로 판명
        return commentRepository.countByParentCommentId(comment.getArticleId(), comment.getCommentId(), 2L) == 2;
    }

    private void delete(Comment comment){
        commentRepository.delete(comment);
        if(!comment.isRoot()){
            // 자기 자신이 루트가 아닐 때 -> 상위댓글이 존재할 때
            // -> 상위 댓글을 재기적으로 삭제해야함
            commentRepository.findById(comment.getParentCommentId())
                    // 삭제 대상인지 확인 필요
                    // 삭제 대상아니면 삭제 불가능
                    .filter(Comment::getDeleted)
                    // 또 다른 자식을 가졌는 지 확인 필요
                    // 또 다른 자식이 존재할 시 삭제 불가능
                    .filter(Predicate.not(this::hasChildren))
                    // 모든 조건 만족 시, 삭제 진행
                    // delete 함수를 재귀 호출하여 삭제
                    // recursive
                    .ifPresent(this::delete);
        }
    }

}

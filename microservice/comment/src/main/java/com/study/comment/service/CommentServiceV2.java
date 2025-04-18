package com.study.comment.service;

import com.study.comment.entity.Comment;
import com.study.comment.entity.CommentPath;
import com.study.comment.entity.CommentV2;
import com.study.comment.repository.CommentRepositoryV2;
import com.study.comment.request.CommentCreateRequestV2;
import com.study.comment.response.CommentPageResponse;
import com.study.comment.response.CommentResponse;
import com.study.comment.util.PageLimitCalculator;
import com.study.snowflake.Snowflake;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Predicate;

import static java.util.function.Predicate.not;

@Service
@RequiredArgsConstructor
public class CommentServiceV2 {
    Snowflake snowflake = new Snowflake();
    private final CommentRepositoryV2 commentRepositoryV2;

    @Transactional
    public CommentResponse create(CommentCreateRequestV2 request) {
        // 상의 댓글을 찾아 하위 댓글의 path를 부여
        CommentV2 parent = findParent(request);
        CommentPath parentCommentPath = parent == null ? CommentPath.create("") : parent.getCommentPath();
        CommentV2 commentV2 = commentRepositoryV2.save(CommentV2.create(
                snowflake.nextId(),
                request.getContent(),
                request.getArticleId(),
                request.getWriterId(),
                parentCommentPath.createChildCommentPath(
                        commentRepositoryV2.findDescendantsTopPath(request.getArticleId(), parentCommentPath.getPath())
                                .orElse(null)))
        );

        return CommentResponse.from(commentV2);
    }

    private CommentV2 findParent(CommentCreateRequestV2 request) {
        String parentPath = request.getParentPath();
        if(parentPath == null) {
            return null;
        }

        return commentRepositoryV2.findByPath(parentPath)
                .filter(not(CommentV2::getDeleted))
                .orElseThrow();
    }

    public CommentResponse read(Long commentId){
        return CommentResponse.from(
                commentRepositoryV2.findById(commentId).orElseThrow());
    }

    @Transactional
    public void delete(Long commentId){
        commentRepositoryV2.findById(commentId)
                .filter(CommentV2::getDeleted)
                .ifPresent(comment -> {
                    if(hasChildren(comment)){
                        // 자식이 존재할 경우, 삭제 표시만
                        comment.delete();
                    }
                    else{
                        // 자식 없을 경우, 데이터 삭제
                        delete(comment);
                    }

                });
    }

    private boolean hasChildren(CommentV2 comment) {
        // 자식 댓글이 존재하는지 확인
        return commentRepositoryV2.findDescendantsTopPath(
                comment.getArticleId(),
                comment.getCommentPath().getPath()
        ).isPresent();
    }

    private void delete(CommentV2 comment){
        commentRepositoryV2.delete(comment);
        if(!comment.isRoot()){
            // 자기 자신이 루트가 아닐 때 -> 상위댓글이 존재할 때
            // -> 상위 댓글을 재기적으로 삭제해야함
            commentRepositoryV2.findByPath(comment.getCommentPath().getParentPath())
                    // 삭제 대상인지 확인 필요
                    // 삭제 대상아니면 삭제 불가능
                    .filter(CommentV2::getDeleted)
                    // 또 다른 자식을 가졌는 지 확인 필요
                    // 또 다른 자식이 존재할 시 삭제 불가능
                    .filter(not(this::hasChildren))
                    // 모든 조건 만족 시, 삭제 진행
                    // delete 함수를 재귀 호출하여 삭제
                    // recursive
                    .ifPresent(this::delete);
        }
    }
    public CommentPageResponse readAll(Long articleId, Long page, Long pageSize){
        List<CommentResponse> commentResponses = commentRepositoryV2.findAll(articleId, (page - 1) * pageSize, pageSize).stream()
                .map(CommentResponse::from)
                .toList();
        Long count = commentRepositoryV2.countByArticleId(articleId, PageLimitCalculator.calculatePageLimit(page, pageSize, 10L));
        return CommentPageResponse.from(commentResponses, count);
    }

    public List<CommentResponse> readAllInfiniteScroll(Long articleId, String lastPath, Long limit){
        // 첫번째 페이징 요청이라면?
        List<CommentV2> comments = lastPath == null ?
                commentRepositoryV2.findAllInifiteScroll(articleId, limit) :
                commentRepositoryV2.findAllInifiteScroll(articleId, lastPath, limit);

        return comments.stream()
                .map(CommentResponse::from)
                .toList();
    }
}

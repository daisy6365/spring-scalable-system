package com.study.comment.api;

import com.study.comment.response.CommentPageResponse;
import com.study.comment.response.CommentResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.RestClient;

import java.util.List;

@Slf4j
public class CommentApiTest {
    RestClient restClient = RestClient.create("http://127.0.0.1:9001");

    @Test
    void createTest() {
        CommentResponse response = createComment(new CommentCreateRequest(1L, "my comment1", null, 1L));
        CommentResponse response2 = createComment(new CommentCreateRequest(1L, "my comment2", response.getCommentId(), 1L));
        CommentResponse response3 = createComment(new CommentCreateRequest(1L, "my comment3", response.getCommentId(), 1L));

        log.info("[1] commentId = {}", response.getCommentId());
        log.info("[2] commentId = {}", response2.getCommentId());
        log.info("[3] commentId = {}", response3.getCommentId());
        /**
         * [1] commentId = 168180303607812096
         * [2] commentId = 168180304299872256
         * [3] commentId = 168180304350203904
         */
    }

    @Test
    void readTest() {
        CommentResponse commentResponse = readComment();
        log.info("commentResponse = {}", commentResponse);
    }

    @Test
    void deleteTest() {
        deleteComment();
    }

    @Test
    void readAllTest(){
        CommentPageResponse response = readAllComments();

        log.info("count = {}", response.getCommentCount());
        for (CommentResponse comment : response.getComments()) {
            if(!comment.getCommentId().equals(comment.getParentCommentId())){
                log.info("\t - ");
            }
            log.info("commentResponse = {}", comment.getCommentId());
        }
    }

    @Test
    void readAllInfiniteScrollTest(){
        List<CommentResponse> commentResponses1 = readAllInfiniteScroll1();
        for (CommentResponse comment : commentResponses1) {
            log.info("response Comment ID = {}", comment.getCommentId());
        }

        CommentResponse last = commentResponses1.getLast();
        Long lastParentCommentId = last.getParentCommentId();
        Long lastCommentId = last.getCommentId();
        log.info("Last parentComment id = {}, Last comment Id = {}", lastParentCommentId, lastCommentId);
        List<CommentResponse> commentResponses = readAllInfiniteScroll2(lastParentCommentId, lastCommentId);
        for (CommentResponse article : commentResponses) {
            log.info("response Article ID = {}", article.getArticleId());
        }
    }

    CommentResponse createComment(CommentCreateRequest request) {
        return restClient.post()
                .uri("/v1/comments")
                .body(request)
                .retrieve()
                .body(CommentResponse.class);
    }

    CommentResponse readComment() {
        return restClient.get()
                .uri("/v1/comments/{commentId}", 168180303607812096L)
                .retrieve()
                .body(CommentResponse.class);
    }

    void deleteComment() {
        restClient.delete()
                .uri("/v1/comments/{commentId}", 168180304350203904L)
                .retrieve();
    }

    CommentPageResponse readAllComments(){
        return restClient.get()
                .uri("/v1/comments?articleId=1&page=1&pageSize=10")
                .retrieve()
                .body(CommentPageResponse.class);
    }

    List<CommentResponse> readAllInfiniteScroll1(){
        return restClient.get()
                .uri(("/v1/comments/infinite-scroll?articleId=%s&" +
                        "pageSize=%s").formatted(1L, 5L))
                .retrieve()
                .body(new ParameterizedTypeReference<List<CommentResponse>>() {});
    }

    List<CommentResponse> readAllInfiniteScroll2(Long lastParentCommentId, Long lastCommentId){
        return restClient.get()
                .uri(("/v1/comments/infinite-scroll?articleId=%s&" +
                        "lastParentCommentId=%s&" +
                        "lastCommentId=%s&" +
                        "pageSize=%s").formatted(1L, lastParentCommentId, lastCommentId, 5L))
                .retrieve()
                .body(new ParameterizedTypeReference<List<CommentResponse>>() {});
    }


    @Getter
    @AllArgsConstructor
    public static class CommentCreateRequest {
        private Long articleId;
        private String content;
        private Long parentCommentId;
        private Long writerId;
    }

}

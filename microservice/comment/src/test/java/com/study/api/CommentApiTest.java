package com.study.api;

import com.study.comment.response.CommentResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

@Slf4j
public class CommentApiTest {
    RestClient restClient = RestClient.create("http://127.0.0.1:9001");

    @Test
    void create() {
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
    void read() {
        CommentResponse commentResponse = readComment();
        log.info("commentResponse = {}", commentResponse);
    }

    @Test
    void delete() {
        deleteComment();
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


    @Getter
    @AllArgsConstructor
    public static class CommentCreateRequest {
        private Long articleId;
        private String content;
        private Long parentCommentId;
        private Long writerId;
    }

}

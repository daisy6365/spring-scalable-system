package com.study.comment.api;

import com.study.comment.response.CommentResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

@Slf4j
public class CommentApiV2Test {
    RestClient restClient = RestClient.create("http://127.0.0.1:9001");

    @Test
    void createTest() {
        CommentResponse response = createComment(new CommentCreateRequestV2(1L, "my comment1", null, 1L));
        CommentResponse response2 = createComment(new CommentCreateRequestV2(1L, "my comment2", response.getPath(), 1L));
        CommentResponse response3 = createComment(new CommentCreateRequestV2(1L, "my comment3", response2.getPath(), 1L));

        log.info("[1] commentId = {}", response.getCommentId());
        log.info("[1] path = {}", response.getPath());
        log.info("[2] commentId = {}", response2.getCommentId());
        log.info("[2] path = {}", response2.getPath());
        log.info("[3] commentId = {}", response3.getCommentId());
        log.info("[3] path = {}", response3.getPath());
        /**
         * [1] commentId = 171247976439660544
         * [1] path = 00002
         * [2] commentId = 171247977098166272
         * [2] path = 0000200000
         * [3] commentId = 171247977186246656
         * [3] path = 000020000000000
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

    CommentResponse createComment(CommentCreateRequestV2 request) {
        return restClient.post()
                .uri("/v2/comments")
                .body(request)
                .retrieve()
                .body(CommentResponse.class);
    }

    CommentResponse readComment() {
        return restClient.get()
                .uri("/v2/comments/{commentId}", 171247976439660544L)
                .retrieve()
                .body(CommentResponse.class);
    }

    void deleteComment() {
        restClient.delete()
                .uri("/v2/comments/{commentId}", 171247976439660544L)
                .retrieve();
    }

    @Getter
    @AllArgsConstructor
    public static class CommentCreateRequestV2 {
        private Long articleId;
        private String content;
        private String parentPath;
        private Long writerId;
    }


}

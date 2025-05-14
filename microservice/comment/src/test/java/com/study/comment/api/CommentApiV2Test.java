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


    @Test
    void readAllTest(){
        CommentPageResponse response = readAllComments();

        log.info("count = {}", response.getCommentCount());
        for (CommentResponse comment : response.getComments()) {
            log.info("commentResponse = {}", comment.getCommentId());
        }

        /**
         * count = 101
         * commentResponse = 171498277123817472
         * commentResponse = 171498277182537729
         * commentResponse = 171498277182537752
         * commentResponse = 171498277182537761
         * commentResponse = 171498277182537767
         * commentResponse = 171498277182537773
         * commentResponse = 171498277186732037
         * commentResponse = 171498277186732044
         * commentResponse = 171498277186732053
         * commentResponse = 171498277186732061
         */
    }

    @Test
    void readAllInfiniteScrollTest(){
        List<CommentResponse> commentResponses1 = readAllInfiniteScroll1();
        for (CommentResponse comment : commentResponses1) {
            log.info("response Comment ID = {}", comment.getPath());
        }
        String lastPath = commentResponses1.getLast().getPath();
        log.info("reponse Comment last Path = {}", lastPath);

        List<CommentResponse> commentResponses = readAllInfiniteScroll2(lastPath);
        for (CommentResponse article : commentResponses) {
            log.info("response Comment ID = {}", article.getPath());
        }

        /**
         * response Comment ID = 00000
         * response Comment ID = 00001
         * response Comment ID = 00002
         * response Comment ID = 00003
         * response Comment ID = 00004
         * reponse Comment last Path = 00004
         * response Comment ID = 00005
         * response Comment ID = 00006
         * response Comment ID = 00007
         * response Comment ID = 00008
         * response Comment ID = 00009
         */
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

    CommentPageResponse readAllComments(){
        return restClient.get()
                .uri("/v2/comments?articleId=1&page=1&pageSize=10")
                .retrieve()
                .body(CommentPageResponse.class);
    }

    List<CommentResponse> readAllInfiniteScroll1(){
        return restClient.get()
                .uri(("/v2/comments/infinite-scroll?articleId=%s&" +
                        "pageSize=%s").formatted(1L, 5L))
                .retrieve()
                .body(new ParameterizedTypeReference<List<CommentResponse>>() {});
    }

    List<CommentResponse> readAllInfiniteScroll2(String lastPath){
        return restClient.get()
                .uri(("/v2/comments/infinite-scroll?articleId=%s&" +
                        "lastPath=%s&" +
                        "pageSize=%s").formatted(1L, lastPath, 5L))
                .retrieve()
                .body(new ParameterizedTypeReference<List<CommentResponse>>() {});
    }


    @Getter
    @AllArgsConstructor
    public static class CommentCreateRequestV2 {
        private Long articleId;
        private String content;
        private String parentPath;
        private Long writerId;
    }

    @Test
    void countTest(){
        CommentResponse response = createComment(new CommentCreateRequestV2(2L, "my content1", null, 1L));

        Long count1 = restClient.get()
                .uri("/v2/comments/articles/{articleId}/count", 2L)
                .retrieve()
                .body(Long.class);

        log.info("count1 = {}", count1);

        restClient.delete()
                .uri("/v2/comments/{commentId}", response.getCommentId())
                .retrieve();

        Long count2 = restClient.get()
                .uri("/v2/comments/articles/{articleId}/count", 2L)
                .retrieve()
                .body(Long.class);

        log.info("count2 = {}", count2);
    }

}

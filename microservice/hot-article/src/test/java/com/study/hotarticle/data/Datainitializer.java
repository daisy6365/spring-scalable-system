package com.study.hotarticle.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

import java.util.random.RandomGenerator;

public class Datainitializer {
    RestClient articleServiceClient = RestClient.create("http://localhost:9000");
    RestClient commentServiceClient = RestClient.create("http://localhost:9001");
    RestClient likeServiceClient = RestClient.create("http://localhost:9002");
    RestClient viewServiceClient = RestClient.create("http://localhost:9003");

    @Test
    void initialize(){
        for (int i = 0; i < 10; i++) {
            // 게시글 생성
            Long articleId = createArticle();
            // 해당 게시글에 대한 댓글, 좋아요, 조회 수 생성
            long commentCount = RandomGenerator.getDefault().nextLong(10);
            long likeCount = RandomGenerator.getDefault().nextLong(10);
            long viewCount = RandomGenerator.getDefault().nextLong(200);

            createComment(articleId, commentCount);
            createLike(articleId, likeCount);
            createView(articleId, viewCount);
        }
    }

    Long createArticle() {
        return articleServiceClient.post()
                .uri("/v1/articles")
                .body(new ArticleCreateRequest("title", "content", 1L, 1L))
                .retrieve()
                .body(ArticleResponse.class)
                .getArticleId();
    }

    @Getter
    @AllArgsConstructor
    static class ArticleCreateRequest {
        private String title;
        private String content;
        private Long writerId;
        private Long boardId;
    }

    @Getter
    static class ArticleResponse{
        private Long articleId;
    }

    void createComment(Long articleId, Long commentCount) {
        while(commentCount-- > 0){
            commentServiceClient.post()
                    .uri("/v2/comments")
                    .body(new CommentCreateRequest(articleId, "content", 1L))
                    .retrieve();
        }
    }

    @Getter
    @AllArgsConstructor
    static class CommentCreateRequest {
        private Long articleId;
        private String content;
        private Long writerId;
    }

    void createLike(Long articleId, Long likeCount) {
        while(likeCount-- > 0){
            likeServiceClient.post()
                    .uri("/v1/articles-likes/articles/{articleId}/users/{userId}/pessimistic-lock-1", articleId, likeCount)
                    .retrieve();
        }
    }

    void createView(Long articleId, Long viewCount) {
        while(viewCount-- > 0){
            viewServiceClient.post()
                    .uri("/v1/articles-views/articles/{articleId}/users/{userId}", articleId, viewCount)
                    .retrieve();
        }
    }
}

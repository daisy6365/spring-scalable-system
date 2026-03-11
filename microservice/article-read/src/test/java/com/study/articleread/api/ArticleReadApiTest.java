package com.study.articleread.api;

import com.study.articleread.service.response.ArticleReadPageResponse;
import com.study.articleread.service.response.ArticleReadResponse;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.RestClient;

import java.util.List;

public class ArticleReadApiTest {
    RestClient articleReadRestClient = RestClient.create("http://localhost:9005");
    RestClient articleRestClient = RestClient.create("http://localhost:9000");

    @Test
    void restTest(){
        ArticleReadResponse response = articleReadRestClient.get()
                .uri("/v1/articles/{articleId}", 289639203135139840L)
                .retrieve()
                .body(ArticleReadResponse.class);

        System.out.println("[ArticleReadApiTest.readTest] response = " + response);
    }

    @Test
    void restAllTest(){
        ArticleReadPageResponse response1 = articleReadRestClient.get()
                .uri("/v1/articles?boardId=%s&page=%s&pageSize=%s".formatted(1L, 3000L, 5))
                .retrieve()
                .body(ArticleReadPageResponse.class);

        System.out.println("[response1.getArticleCount() = " + response1.getArticleCount());
        for (ArticleReadResponse article : response1.getArticles()) {
            System.out.println("article.getArticleId() = " + article.getArticleId());
        }

        // 원본데이터로부터 가져와서 동일한 결과값인지 확인
        ArticleReadPageResponse response2 = articleRestClient.get()
                .uri("/v1/articles?boardId=%s&page=%s&pageSize=%s".formatted(1L, 3000L, 5))
                .retrieve()
                .body(ArticleReadPageResponse.class);

        System.out.println("[response2.getArticleCount() = " + response2.getArticleCount());
        for (ArticleReadResponse article : response2.getArticles()) {
            System.out.println("article.getArticleId() = " + article.getArticleId());
        }
    }

    @Test
    void readAllInfiniteScrollTest(){
        List<ArticleReadResponse> response1 = articleReadRestClient.get()
                .uri("/v1/articles/infinite-scroll?boardId=%s&lastArticleId=%s&pageSize=%s".formatted(1L, 290008650694283264L, 5L))
                .retrieve()
                .body(new ParameterizedTypeReference<List<ArticleReadResponse>>() {
                });
        for (ArticleReadResponse response : response1) {
            System.out.println("response = " + response.getArticleId());
        }

        // 원본데이터로부터 가져와서 동일한 결과값인지 확인
        List<ArticleReadResponse> response2 = articleRestClient.get()
                .uri("/v1/articles/infinite-scroll?boardId=%s&lastArticleId=%s&pageSize=%s".formatted(1L, 290008650694283264L, 5L))
                .retrieve()
                .body(new ParameterizedTypeReference<List<ArticleReadResponse>>() {
                });
        for (ArticleReadResponse response : response2) {
            System.out.println("response = " + response.getArticleId());
        }
    }
}

package com.study.articleread.api;

import com.study.articleread.service.response.ArticleReadResponse;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

public class ArticleReadApiTest {
    RestClient restClient = RestClient.create("http://localhost:9005");

    @Test
    void restTest(){
        ArticleReadResponse response = restClient.get()
                .uri("/v1/articles/{articleId}", 289639203135139840L)
                .retrieve()
                .body(ArticleReadResponse.class);

        System.out.println("[ArticleReadApiTest.readTest] response = " + response);
    }
}

package com.study.article.api;

import com.study.article.response.ArticleResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

@Slf4j
public class ArticleApiTest {
    RestClient restClient = RestClient.create("http://localhost:9000");

    @Test
    @DisplayName("게시글을 등록한다.")
    void createTest(){
        ArticleCreateRequest request = new ArticleCreateRequest("hi", "my content", 1L, 1L);
        ArticleResponse response = create(request);

        log.info(String.valueOf(response));
    }

    ArticleResponse create(ArticleCreateRequest request){
        return restClient.post()
                .uri("/v1/articles")
                .body(request)
                .retrieve()
                .body(ArticleResponse.class);
    }

    @Getter
    @AllArgsConstructor
    public static class ArticleCreateRequest {
        private String title;
        private String content;
        private Long writerId;
        private Long boardId;
    }

    @Test
    @DisplayName("게시글을 조회한다.")
    void readTest(){
        ArticleResponse response = read(166444668654870528L);
        log.info(String.valueOf(response));
    }

    ArticleResponse read(Long articleId){
        return restClient.get()
                .uri("/v1/articles/{articleId}", articleId)
                .retrieve()
                .body(ArticleResponse.class);
    }


    @Test
    @DisplayName("게시글을 수정한다.")
    void updateTest(){
        ArticleUpdateRequest request = new ArticleUpdateRequest("hi update", "my content update");
        update(166444668654870528L, request);
        read(166444668654870528L);
    }

    ArticleResponse update(Long articleId,ArticleUpdateRequest request){
        return restClient.put()
                .uri("/v1/articles/{articleId}", articleId)
                .body(request)
                .retrieve()
                .body(ArticleResponse.class);
    }


    @Getter
    @AllArgsConstructor
    public class ArticleUpdateRequest {
        private String title;
        private String content;
    }

    @Test
    @DisplayName("게시글을 삭제한다.")
    void deleteTest(){
        delete(166444668654870528L);
    }

    void delete(Long articleId){
        restClient.delete()
                .uri("/v1/articles/{articleId}", articleId)
                .retrieve();
    }

}

package com.study.article.api;

import com.study.article.response.ArticlePageResponse;
import com.study.article.response.ArticleResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.RestClient;

import java.util.List;

@Slf4j
public class ArticleApiTest {
    RestClient restClient = RestClient.create("http://localhost:9000");

    @Getter
    @AllArgsConstructor
    public static class ArticleCreateRequest {
        private String title;
        private String content;
        private Long writerId;
        private Long boardId;
    }

    @Getter
    @AllArgsConstructor
    public class ArticleUpdateRequest {
        private String title;
        private String content;
    }

    @Test
    @DisplayName("게시글을 등록한다.")
    void createTest(){
        ArticleCreateRequest request = new ArticleCreateRequest("hi", "my content", 1L, 1L);
        ArticleResponse response = create(request);

        log.info(String.valueOf(response));
    }

    @Test
    @DisplayName("게시글을 조회한다.")
    void readTest(){
        ArticleResponse response = read(166444668654870528L);
        log.info(String.valueOf(response));
    }

    @Test
    @DisplayName("게시글을 수정한다.")
    void updateTest(){
        ArticleUpdateRequest request = new ArticleUpdateRequest("hi update", "my content update");
        update(166444668654870528L, request);
        read(166444668654870528L);
    }

    @Test
    @DisplayName("게시글을 삭제한다.")
    void deleteTest(){
        delete(166444668654870528L);
    }

    @Test
    @DisplayName("게시글을 페이징 조회한다.")
    void readAllTest(){
        ArticlePageResponse response = readAll();
        log.info("response Article count = {}", response.getArticleCount());
        for (ArticleResponse article : response.getArticles()) {
            log.info("response Article ID = {}", article.getArticleId());
        }
    }

    @Test
    @DisplayName("게시글을 무한스크롤 페이징 조회한다.")
    void readAllInfiniteScrollTest(){
        List<ArticleResponse> articleResponses1 = readAllInfiniteScroll1();
        for (ArticleResponse article : articleResponses1) {
            log.info("response Article ID = {}", article.getArticleId());
        }

        Long articleId = articleResponses1.getLast().getArticleId();
        log.info("Last article id = {}", articleId);
        List<ArticleResponse> articleResponses = readAllInfiniteScroll2(articleId);
        for (ArticleResponse article : articleResponses) {
            log.info("response Article ID = {}", article.getArticleId());
        }
    }

    ArticleResponse create(ArticleCreateRequest request){
        return restClient.post()
                .uri("/v1/articles")
                .body(request)
                .retrieve()
                .body(ArticleResponse.class);
    }

    ArticleResponse read(Long articleId){
        return restClient.get()
                .uri("/v1/articles/{articleId}", articleId)
                .retrieve()
                .body(ArticleResponse.class);
    }

    ArticleResponse update(Long articleId,ArticleUpdateRequest request){
        return restClient.put()
                .uri("/v1/articles/{articleId}", articleId)
                .body(request)
                .retrieve()
                .body(ArticleResponse.class);
    }

    void delete(Long articleId){
        restClient.delete()
                .uri("/v1/articles/{articleId}", articleId)
                .retrieve();
    }

    ArticlePageResponse readAll(){
        return restClient.get()
                .uri("/v1/articles?boardId=1&page=50000&pageSize=30")
                .retrieve()
                .body(ArticlePageResponse.class);


    }

    List<ArticleResponse> readAllInfiniteScroll1(){
        return restClient.get()
                .uri("/v1/articles/infinite-scroll?boardId=1&pageSize=30")
                .retrieve()
                .body(new ParameterizedTypeReference<List<ArticleResponse>>() {});
    }

    List<ArticleResponse> readAllInfiniteScroll2(Long lastArticleId){
        return restClient.get()
                .uri("/v1/articles/infinite-scroll?boardId=1&pageSize=30&lastArticleId=%s".formatted(lastArticleId))
                .retrieve()
                .body(new ParameterizedTypeReference<List<ArticleResponse>>() {});
    }

}

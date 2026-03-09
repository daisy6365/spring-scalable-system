package com.study.articleread.client;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * 원본데이터가 없을 때,
 * Command Server로 데이터를 요청하기 위한
 * Client 정의 -> 댓글용
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CommentClient {
    private RestClient restClient;
    @Value("${endpoints.spring-scalable-system-comment-service.url}")
    private String commentServiceUrl;

    @PostConstruct
    public void initRestClient(){
        restClient = RestClient.create(commentServiceUrl);
    }

    public long count(Long articleId){
        try{
            return restClient.get()
                    .uri("/v2/comments/articles/{articleId}/count", articleId)
                    .retrieve()
                    .body(Long.class);
        } catch (Exception e) {
            log.error("[CommentClient.count] articleId = {}", articleId, e);
            return 0;
        }
    }

}

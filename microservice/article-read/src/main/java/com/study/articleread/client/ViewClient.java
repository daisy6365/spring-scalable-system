package com.study.articleread.client;

import com.study.articleread.cache.OptimizedCacheable;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

/**
 * 원본데이터가 없을 때,
 * Command Server로 데이터를 요청하기 위한
 * Client 정의 -> 댓글용
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ViewClient {
    private RestClient restClient;
    @Value("${endpoints.spring-scalable-system-view-service.url}")
    private String viewServiceUrl;

    @PostConstruct
    public void initRestClient(){
        restClient = RestClient.create(viewServiceUrl);
    }

    /**
     * @Cacheable 내부 동작방식
     * Redis에서 데이터를 조회
     * i) 데이터가 존재하지 않는다면
     * count() 메소드 내부 로직이 호출
     * -> viewService로 원본 데이터를 요청
     * -> 이후 Redis에 데이터에 넣음
     *
     * ii) 데이터가 존재한다면
     * 해당 데이터를 그대로 반환, count() 메소드는 수행하지 않음
     */
//    @Cacheable(key = "#articleId", value = "articleViewCount")
    @OptimizedCacheable(type = "articleViewCount", ttlSeconds = 1)
    public long count(Long articleId){
        log.info("[ViewClient.count] articleId = {}", articleId);
        try{
            return restClient.get()
                    .uri("/v1/articles-views/articles/{articleId}/count", articleId)
                    .retrieve()
                    .body(Long.class);
        } catch (Exception e) {
            log.error("[ViewClient.count] articleId = {}", articleId, e);
            return 0;
        }
    }

}

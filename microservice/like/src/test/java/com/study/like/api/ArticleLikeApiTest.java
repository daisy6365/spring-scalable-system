package com.study.like.api;

import com.study.like.response.ArticleLikeResponse;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class ArticleLikeApiTest {
    RestClient restClient = RestClient.create("http://localhost:9002");

    @Test
    void likeAndUnlikeTest() {
        Long articleId = 9999L;

        like(articleId, 1L);
        like(articleId, 2L);
        like(articleId, 3L);

        ArticleLikeResponse response1 = read(articleId, 1L);
        ArticleLikeResponse response2 = read(articleId, 2L);
        ArticleLikeResponse response3 = read(articleId, 3L);

        log.info("response1 = {}", response1);
        log.info("response2 = {}", response2);
        log.info("response3 = {}", response3);

        unlike(articleId, 1L);
        unlike(articleId, 2L);
        unlike(articleId, 3L);

    }

    void like(Long articleId, Long userId) {
        restClient.post()
                .uri("v1/articles-likes/articles/{articleId}/users/{userId}", articleId, userId)
                .retrieve();
    }

    void unlike(Long articleId, Long userId) {
        restClient.delete()
                .uri("v1/articles-likes/articles/{articleId}/users/{userId}", articleId, userId)
                .retrieve();
    }


    void likeLock(Long articleId, Long userId, String lockType) {
        restClient.post()
                .uri("v1/articles-likes/articles/{articleId}/users/{userId}/" + lockType, articleId, userId)
                .retrieve();
    }

    void unlikeLock(Long articleId, Long userId, String lockType) {
        restClient.delete()
                .uri("v1/articles-likes/articles/{articleId}/users/{userId/}" + lockType, articleId, userId)
                .retrieve();
    }


    ArticleLikeResponse read(Long articleId, Long userId) {
        return restClient.get()
                .uri("v1/articles-likes/articles/{articleId}/users/{userId}", articleId, userId)
                .retrieve()
                .body(ArticleLikeResponse.class);
    }


    @Test
    void likePerformanceTest() throws InterruptedException {
        // 고정된 thread pool 100개를 만듦
        ExecutorService executorService = Executors.newFixedThreadPool(100);

        /**
         * lockType = pessimistic-lock-1, time = 447ms
         * count = 3001
         */
        likePerformanceTest(executorService, 1111L, "pessimistic-lock-1");
        /**
         * lockType = pessimistic-lock-2, time = 630ms
         * count = 3001
         */
        likePerformanceTest(executorService, 2222L, "pessimistic-lock-2");
        /**
         * lockType = optimistic-lock, time = 174ms
         * count = 364
         *
         * 이유 : 동시 요청이 오면, 버전 확인을 통해 충돌 감지를 해서 rollback이 이루어짐
         * Row was updated or deleted by another transaction (or unsaved-value mapping was incorrect)
         */
        likePerformanceTest(executorService, 3333L, "optimistic-lock");
    }

    void likePerformanceTest(ExecutorService executorService, Long articleId, String lockType) throws InterruptedException {
        // 각 API 마다 3000번씩 호출
        CountDownLatch latch = new CountDownLatch(3000);
        log.info("lockType = {}", lockType);

        likeLock(articleId, 1L , lockType);

        // 각 방법마다 속도 차이를 볼 예정임
        // 시간 검증 : 시작 시간 구함
        long start = System.nanoTime();
        // 멀티 스레드로 동시 호출
        for (int i = 0; i < 3000; i++) {
            long userId = i + 2;
            executorService.submit(() -> {
                likeLock(articleId, userId, lockType);
                latch.countDown();
            });
        }
        latch.await();
        // 시간 검증 : 종료 시간 구함
        long end = System.nanoTime();
        log.info("start time = {}, end time = {}", start, end);
        log.info("lockType = {}, time = {}", lockType, (end - start) / 10000000 + "ms");

        Long count = restClient.get()
                .uri("/v1/articles-likes/articles/{articleId}/count", articleId)
                .retrieve()
                .body(Long.class);

        log.info("count = {}", count);
    }
}

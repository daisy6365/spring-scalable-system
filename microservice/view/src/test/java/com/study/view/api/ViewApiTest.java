package com.study.view.api;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.LongAccumulator;

@Slf4j
public class ViewApiTest {
    RestClient restClient = RestClient.create("https://localhost:9003");

    @Test
    void viewTest() throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(100);
        CountDownLatch countDownLatch = new CountDownLatch(10000); // 1만건 생성

        for (int i = 0; i < 10000; i++) {
            executorService.submit(() -> {
                restClient.post()
                        .uri("/v1/article-views/articles/{articleId}/users/{userId}", 1L, 1L)
                        .retrieve();
                countDownLatch.countDown();
            });
        }

        countDownLatch.await();

        Long count = restClient.get()
                .uri("/v1/article-views/articles/{articleId}/users/{userId}", 1L, 1L)
                .retrieve()
                .body(Long.class);

        log.info("count = {}",count.toString());
    }
}

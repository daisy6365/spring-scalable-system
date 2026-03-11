package com.study.articleread.client;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@SpringBootTest
class ViewClientTest {
    @Autowired
    ViewClient viewClient;

    @Test
    void readCacheableTest() throws InterruptedException {
        // 초기 생성 -> count() 수행 -> 로그 출력
        viewClient.count(1L);
        // 1초 내로 재호출 -> count() 미수행 -> 로그 미출력
        viewClient.count(1L);
        // 1초 내로 재호출 -> count() 미수행 -> 로그 미출력
        viewClient.count(1L);

        TimeUnit.SECONDS.sleep(3);
        // 3초 이후 -> Redis에서 삭제 -> count() 수행 -> 로그 출력
        viewClient.count(1L);
    }

    @Test
    void readCacheableMultiThreadTest() throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(5);

        viewClient.count(1L); // init cache

        for (int i = 0; i < 5; i++) {
            CountDownLatch latch = new CountDownLatch(5);

            for (int j = 0; j < 5; j++) {
                executorService.submit(() -> {
                    viewClient.count(1L);
                    latch.countDown();
                });
            }
            latch.await();
            TimeUnit.SECONDS.sleep(2);
            System.out.println("=== cache expired ===");
        }
    }
}
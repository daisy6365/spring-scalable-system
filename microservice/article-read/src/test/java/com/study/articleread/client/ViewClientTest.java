package com.study.articleread.client;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

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
}
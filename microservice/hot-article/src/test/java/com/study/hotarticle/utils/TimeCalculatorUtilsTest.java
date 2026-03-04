package com.study.hotarticle.utils;

import org.junit.jupiter.api.Test;

import java.time.Duration;

class TimeCalculatorUtilsTest {
    @Test
    void test(){
        Duration duration = TimeCalculatorUtils.calculateDurationToMidnight();
        // 현재 test 시각 : 11시 09분
        // 현재로 부터 771분 지나면 자정 (00:00)
        System.out.println("duration.getSeconds() / 60 = " + duration.getSeconds() / 60); // 분단위
    }

}
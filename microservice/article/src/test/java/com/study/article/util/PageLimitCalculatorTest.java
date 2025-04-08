package com.study.article.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PageLimitCalculatorTest {
    @Test
    void calculatePageLimitTest() {
        /**
         * 현재 페이지가 1
         * 페이지당 30개를 호출
         * 이동가능한 페이지가 10개 라면?
         * **총 301개** 가 조회되어야 함
         */
        calculatePageLimitTest(1L, 30L, 10L, 301L);
        calculatePageLimitTest(7L, 30L, 10L, 301L);
        calculatePageLimitTest(10L, 30L, 10L, 301L);
        calculatePageLimitFalseTest(11L, 30L, 10L, 301L);
        calculatePageLimitTest(12L, 30L, 10L, 601L);
    }

    // 기대값과 맞는지 검증
    void calculatePageLimitTest(Long page, Long pageSize, Long movablePageCount, Long expected) {
        Long result = PageLimitCalculator.calculatePageLimit(page, pageSize, movablePageCount);
        assertEquals(expected, result);
    }

    // 기대값과 맞는지 검증
    void calculatePageLimitFalseTest(Long page, Long pageSize, Long movablePageCount, Long expected) {
        Long result = PageLimitCalculator.calculatePageLimit(page, pageSize, movablePageCount);
        assertNotEquals(expected, result);
    }
}
package com.study.hotarticle.utils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class TimeCalculatorUtils {
    // ttl - 자정까지 얼마나 남았는지 계산하여 반환
    public static Duration calculateDurationToMidnight() {
        LocalDateTime now = LocalDateTime.now();
        // 자정에 대한 LocalDateTime - 00:00시에 대해 + 1일 한것을 계산
        LocalDateTime midnight = now.plusDays(1).with(LocalTime.MIDNIGHT);
        // 현재 ~ 자정까지 남은 시간을 계산
        return Duration.between(now, midnight);
    }
}

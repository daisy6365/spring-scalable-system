package com.study.hotarticle.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;

/**
 * 오늘의 인기글을 계산하는 동안, 필요한 데이터들을 가지고 있음
 * -> 가지고 있지 않으면 해당 글의 해당 데이터를 어떻게 알지??인거임
 * -> 자체적으로 데이터를 보관해야함
 * [조회 수 저장]
 */
@Repository
@RequiredArgsConstructor
public class ArticleViewCountRepository {
    private final StringRedisTemplate redisTemplate;

    // hot-article::article::{articleId}::view-count
    private static final String KEY_FORMAT = "hot-article::article::%s::view-count";

    public void createOrUpdate(Long articleId, Long viewCount, Duration ttl) {
        redisTemplate.opsForValue().set(generateKey(articleId), String.valueOf(viewCount), ttl);
    }

    public Long read(Long articleId) {
        String result = redisTemplate.opsForValue().get(generateKey(articleId));
        return result == null ? 0L : Long.valueOf(result);
    }

    private String generateKey(Long articleId) {
        return KEY_FORMAT.formatted(articleId);
    }
}

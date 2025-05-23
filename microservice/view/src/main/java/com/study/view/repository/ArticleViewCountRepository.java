package com.study.view.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ArticleViewCountRepository {
    private final StringRedisTemplate redisTemplate;

    // view::article::{article_id}::view_count
    private static final String KEY_FORMAT = "view::article::%s::view_count";

    // 조회수를 읽는 메소드
    public Long read(Long articleId) {
        String result = redisTemplate.opsForValue().get(generatedKey(articleId));

        return result == null ? null : Long.parseLong(result);
    }

    // 조회수 증가 메소드
    public Long increase(Long articleId) {
        return redisTemplate.opsForValue().increment(generatedKey(articleId));
    }

    // Key 생성
    private String generatedKey(Long articleId) {
        return KEY_FORMAT.formatted(articleId);
    }
}

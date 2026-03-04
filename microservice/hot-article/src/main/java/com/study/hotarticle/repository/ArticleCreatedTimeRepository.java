package com.study.hotarticle.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * 오늘 생성된 게시글만 인기글로 채택되어야함
 * 좋아요 Event -> 이 Event에 대한 게시글이 오늘인지 검증 필요
 * -> 게시글 서비스 조회가 필요
 * -> BUT, 게시글 생성 시간 까지 저장하면, 조회하지 않아도 바로 알수 있음
 */
@Repository
@RequiredArgsConstructor
public class ArticleCreatedTimeRepository {
    private final StringRedisTemplate redisTemplate;

    // hot-article::article::{articleId}::created-time
    private static final String KEY_FORMAT = "hot-article::article::%s::create-time";

    public void createOrUpdate(Long articleId, LocalDateTime createAt, Duration ttl) {
        redisTemplate.opsForValue().set(genratedKey(articleId), String.valueOf(createAt.toInstant(ZoneOffset.UTC).toEpochMilli()), ttl);
    }

    public void delete(Long articleId) {
        redisTemplate.delete(genratedKey(articleId));
    }

    public LocalDateTime read(Long articleId) {
        String result = redisTemplate.opsForValue().get(genratedKey(articleId));
        return result == null ? null : LocalDateTime.ofInstant(Instant.ofEpochMilli(Long.valueOf(result)), ZoneOffset.UTC);
    }

    private String genratedKey(Long articleId) {
        return KEY_FORMAT.formatted(articleId);
    }
}

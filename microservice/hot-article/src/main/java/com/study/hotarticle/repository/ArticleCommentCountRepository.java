package com.study.hotarticle.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;

/**
 * 오늘의 인기글을 계산하는 동안, 필요한 데이터들을 가지고 있음
 * -> 가지고 있지 않으면 해당 글의 해당 데이터를 어떻게 알지??인거임
 * -> 자체적으로 데이터를 보관해야함
 * [댓글 수 저장]
 */
@Repository
@RequiredArgsConstructor
public class ArticleCommentCountRepository {
    private final StringRedisTemplate redisTemplate;

    // hot-article::article::{articleId}::comment-count
    private static final String KEY_FORMAT = "hot-article::article::%s::comment-count";

    /**
     * 인기글이 선정될떄 까지만 가지고 있으면 됨
     * -> ttl로 보관
     */
    public void createOrUpdate(Long articleId, Long commentCount, Duration ttl) {
        // set -> 없으면 생성 있으면 update
        redisTemplate.opsForValue().set(generateKey(articleId), String.valueOf(commentCount), ttl);
    }

    public Long read(Long articleId) {
        String result = redisTemplate.opsForValue().get(generateKey(articleId));
        return result == null ? 0L : Long.valueOf(result);
    }

    /**
     * Key -> articleId 로 구분
     */
    private String generateKey(Long articleId) {
        return KEY_FORMAT.formatted(articleId);
    }
}

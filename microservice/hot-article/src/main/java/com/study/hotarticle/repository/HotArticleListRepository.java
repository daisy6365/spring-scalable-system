package com.study.hotarticle.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.StringRedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 게시글 아이디만 저장
 * 필요한 데이터는 service에서 한번에 조회
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class HotArticleListRepository {
    private final StringRedisTemplate redisTemplate;

    // hot-article::list::{yyyyMMdd}
    private static final String KEY_FORMAT = "hot-article::list::%s";

    private static final DateTimeFormatter TIME_FORMMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    public void add(Long articleId, LocalDateTime time, Long score, Long limit, Duration ttl){
        // Redis로 한번만 연결하여 여러개의 연산을 한번에 수행
        redisTemplate.executePipelined((RedisCallback<?>) action -> {
            StringRedisConnection conn = (StringRedisConnection)action;
            String key = generateKey(time);
            conn.zAdd(key, score, String.valueOf(articleId));
            conn.zRemRange(key, 0, - limit - 1);
            conn.expire(key, ttl.getSeconds());
            return null;
        });
    }

    /**
     * 게시글이 삭제 -> 인기글집계에서도 반영이 되어야함
     * 게시글이 생성됐던 시간 -> Key
     * 해당 articleId를 삭제
     */
    public void remove(Long articleId, LocalDateTime time){
        redisTemplate.opsForZSet().remove(generateKey(time), String.valueOf(articleId));
    }

    private String generateKey(LocalDateTime time){
        return generateKey(TIME_FORMMATTER.format(time));
    }

    private String generateKey(String dateStr){
        return KEY_FORMAT.formatted(dateStr);
    }

    public List<Long> readAll(String dataStr){
        return redisTemplate.opsForZSet()
                .reverseRangeWithScores(generateKey(dataStr), 0, -1).stream()
                .peek(tuple -> {
                    log.info("[HotArticleRepository.readAll] articleId = {}, score={}", tuple.getScore(), tuple.getValue());
                })
                .map(ZSetOperations.TypedTuple::getValue)
                .map(Long::valueOf)
                .toList();
    }

}

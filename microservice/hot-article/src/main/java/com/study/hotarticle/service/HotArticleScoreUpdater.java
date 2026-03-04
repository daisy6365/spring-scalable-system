package com.study.hotarticle.service;

import com.study.event.Event;
import com.study.event.EventPayload;
import com.study.hotarticle.repository.ArticleCreatedTimeRepository;
import com.study.hotarticle.repository.HotArticleListRepository;
import com.study.hotarticle.service.eventhandler.EventHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 인기글에 대한 점수를 update해주는 서비스
 */
@Component
@RequiredArgsConstructor
public class HotArticleScoreUpdater {
    private final HotArticleListRepository hotArticleListRepository;
    private final HotArticleScoreCalculator hotArticleScoreCalculator;
    // 오늘 생성된 게시글에 대한 Event인지 확인
    private final ArticleCreatedTimeRepository articleCreatedTimeRepository;

    // 인기글은 최대 10개까지만 저장 -> 상수화
    private static final long HOT_ARTICLE_COUNT = 10;
    // 인기글은 일주일동안 저장, 넉넉하게 10일로 지정 -> 상수화
    private static final Duration HOT_ARTICLE_TTL = Duration.ofDays(10);


    public void update(Event<EventPayload> event, EventHandler<EventPayload> eventHandler) {
        // 해당 이벤트에 대한 게시글 ID 추출
        Long articleId = eventHandler.findArticleId(event);
        // 해당 게시글의 생성시간 추출
        LocalDateTime createdTime = articleCreatedTimeRepository.read(articleId);

        if(!isArticleCreatedToday(createdTime)){
            // 해당 게시글의 생성시간이 오늘이 아니라면, 계산할 필요 X
            return;
        }

        // 인기글 측에서 댓글수, 조회수, 좋아요수 저장해두기
        // handler에서 repository에 데이터 넣어줌
        eventHandler.handle(event);

        // 인기글점수 계산
        long score = hotArticleScoreCalculator.calculate(articleId);
        // redis에 점수 업데이트
        hotArticleListRepository.add(articleId, createdTime, score, HOT_ARTICLE_COUNT, HOT_ARTICLE_TTL);

    }

    private boolean isArticleCreatedToday(LocalDateTime createdTime) {
        return createdTime != null && createdTime.toLocalDate().equals(LocalDate.now());
    }
}

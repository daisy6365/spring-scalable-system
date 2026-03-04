package com.study.hotarticle.service;

import com.study.event.Event;
import com.study.event.EventPayload;
import com.study.event.EventType;
import com.study.hotarticle.client.ArticleClient;
import com.study.hotarticle.repository.HotArticleListRepository;
import com.study.hotarticle.service.eventhandler.EventHandler;
import com.study.hotarticle.service.response.HotArticleResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class HotArticleService {
    // 인기글 조회할 때 ArticleClient 해당 게시글에 대한 데이터들을 가져옴
    private final ArticleClient articleClient;
    // List로 주입 -> EventHandler 구현체들이 다 같이 주입됨
    private final List<EventHandler> eventHandlers;
    private final HotArticleScoreUpdater hotArticleScoreUpdater;
    // 인기글 조회
    private final HotArticleListRepository hotArticleListRepository;


    /**
     * Event를 통해 인기글 점수를 계산 -> hotArticleListRepository에 인기글 정보 저장
     */
    public void handleEvent(Event<EventPayload> event){
        EventHandler<EventPayload> eventHandler = findEventHandler(event);
        if(eventHandler == null){
            return;
        }
        // 생성 Event 인지 삭제 Event 인지 검증
        if(isArticleCreatedOrDeleted(event)){
            // 이벤트 핸들 그대로
            eventHandler.handle(event);
        }
        else{
            // 해당 글에대한 점수를 재계산 해야함
            hotArticleScoreUpdater.update(event, eventHandler);
        }
    }

    /**
     * Event에 대응하는 EventHandler를 매칭
     */
    private EventHandler<EventPayload> findEventHandler(Event<EventPayload> event){
        // stream으로 돌면서 지원하는건지 검증 필터링
        return eventHandlers.stream()
                .filter(eventHandler -> eventHandler.supports(event))
                .findAny()
                .orElse(null);
    }

    private boolean isArticleCreatedOrDeleted(Event<EventPayload> event){
        return EventType.ARTICLE_CREATED == event.getType() || EventType.ARTICLE_DELETED == event.getType();
    }

    public List<HotArticleResponse> readAll(String dateStr){
        // yyyyMMdd
        return hotArticleListRepository.readAll(dateStr).stream()
                .map(articleClient::read)
                .filter(Objects::nonNull)
                .map(HotArticleResponse::from)
                .toList();

    }
}

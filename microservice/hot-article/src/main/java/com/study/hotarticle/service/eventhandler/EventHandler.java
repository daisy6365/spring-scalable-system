package com.study.hotarticle.service.eventhandler;

import com.study.event.Event;
import com.study.event.EventPayload;

/**
 * @param <T> : generic으로 EventPayload를 받음
 * -> 들어오는 Event가 각각 다르기 때문에, 타입별로 안전하게 처리하고자 함
 */
public interface EventHandler <T extends EventPayload> {
    // Event가 들어왔을때 처리
    void handle(Event<T> event);
    // EventHandler가 들어오는 Event를 지원하는지 검증
    boolean supports(Event<T> event);
    // 이벤트에 대한 게시글ID
    Long findArticleId(Event<T> event);
}

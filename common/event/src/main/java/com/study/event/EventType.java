package com.study.event;

import com.study.event.payload.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@RequiredArgsConstructor
public enum EventType {
    // 각각의 event 타입에 대해 payload를 가짐
    ARTICLE_CREATED(ArticleCreatedEventPayload.class, Topic.SCALABLE_ARTICLE),
    ARTICLE_UPDATED(ArticleUpdatedEventPayload.class, Topic.SCALABLE_ARTICLE),
    ARTICLE_DELETED(ArticleDeletedEventPayload.class, Topic.SCALABLE_ARTICLE),
    COMMENT_CREATED(CommentCreatedEventPayload.class, Topic.SCALABLE_COMMENT),
    COMMENT_DELETED(CommentDeletedEventPayload.class, Topic.SCALABLE_COMMENT),
    ARTICLE_LIKE(ArticleLikedEventPayload.class, Topic.SCALABLE_LIKE),
    ARTICLE_UNLIKE(ArticleUnlikedEventPayload.class, Topic.SCALABLE_LIKE),
    ARTICLE_VIEWED(ArticleViewEventPayload.class, Topic.SCALABLE_VIEW),;

    // Event가 어떤 payload type 인지
    private final Class<? extends EventPayload> payloadClass;
    // 어떤 topic으로 전달될 수 있는 지
    private final String topic;

    /**
     * String으로 받은 type -> EventType enum으로 변환
     */
    public static EventType from(String type) {
        try {
            return valueOf(type);
        } catch (Exception e) {
            log.error("[EventType.from] type={}", type, e);
            return null;
        }
    }

    /**
     * topic 정의
     */
    public static class Topic {
        public static final String SCALABLE_ARTICLE = "scalable-article";
        public static final String SCALABLE_COMMENT = "scalable-comment";
        public static final String SCALABLE_LIKE = "scalable-like";
        public static final String SCALABLE_VIEW = "scalable-view";
    }
}

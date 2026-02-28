package com.study.event.payload;

import com.study.event.EventPayload;

import java.time.LocalDateTime;

public class ArticleViewEventPayload implements EventPayload {
    private Long articleId;
    private Long articleViewCount;
}


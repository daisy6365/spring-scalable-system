package com.study.event.payload;

import com.study.event.EventPayload;


public class ArticleViewEventPayload implements EventPayload {
    private Long articleId;
    private Long articleViewCount;
}


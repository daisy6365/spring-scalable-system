package com.study.articleread.service.event.handler;

import com.study.event.Event;
import com.study.event.EventPayload;

public interface EventHandler<T extends EventPayload> {
    void handle(Event<T> event);
    boolean supports(Event<T> event);
}

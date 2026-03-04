package com.study.hotarticle.service;

import com.study.event.Event;
import com.study.event.EventType;
import com.study.hotarticle.service.eventhandler.EventHandler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.stream.Stream;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;


// 단위테스트 화
@ExtendWith(MockitoExtension.class)
class HotArticleServiceTest {
    @InjectMocks
    HotArticleService hotArticleService;
    @Mock
    List<EventHandler> eventHandlers;
    @Mock
    HotArticleScoreUpdater hotArticleScoreUpdater;

    @DisplayName("대응되지 않는 이벤트핸들러는 처리되지 않는다.")
    @Test
    void handlerEventIfEventHandlerNotFoundTest(){
        // given
        Event event = mock(Event.class);
        EventHandler eventHandler = mock(EventHandler.class);
        given(eventHandler.supports(event)).willReturn(false);
        given(eventHandlers.stream()).willReturn(Stream.of(eventHandler));

        // when
        hotArticleService.handleEvent(event);

        // then
        verify(eventHandler, never()).handle(event);
        verify(hotArticleScoreUpdater, never()).update(event, eventHandler);
    }

    @DisplayName("게시글 생성 시, 이벤트핸들러는 호출된다.")
    @Test
    void handlerEventIfArticleCreatedEventTest(){
        // given
        Event event = mock(Event.class);
        given(event.getType()).willReturn(EventType.ARTICLE_CREATED);

        EventHandler eventHandler = mock(EventHandler.class);
        given(eventHandler.supports(event)).willReturn(true);
        given(eventHandlers.stream()).willReturn(Stream.of(eventHandler));

        // when
        hotArticleService.handleEvent(event);

        // then
        verify(eventHandler).handle(event);
        // 게시글 생성은 점수 수정은 호출하지 않음
        verify(hotArticleScoreUpdater, never()).update(event, eventHandler);
    }

    @DisplayName("게시글 삭제 시, 이벤트핸들러는 호출된다.")
    @Test
    void handlerEventIfArticleDeletedEventTest(){
        // given
        Event event = mock(Event.class);
        given(event.getType()).willReturn(EventType.ARTICLE_DELETED);

        EventHandler eventHandler = mock(EventHandler.class);
        given(eventHandler.supports(event)).willReturn(true);
        given(eventHandlers.stream()).willReturn(Stream.of(eventHandler));

        // when
        hotArticleService.handleEvent(event);

        // then
        verify(eventHandler).handle(event);
        // 게시글 생성은 점수 수정은 호출하지 않음
        verify(hotArticleScoreUpdater, never()).update(event, eventHandler);
    }

    @DisplayName("게시글 생성 및 삭제 이외의 이벤트는 점수가 수정된다.")
    @Test
    void handlerEventIfScoreUpdatableEventTest(){
        // given
        Event event = mock(Event.class);
        given(event.getType()).willReturn(mock(EventType.class));

        EventHandler eventHandler = mock(EventHandler.class);
        given(eventHandler.supports(event)).willReturn(true);
        given(eventHandlers.stream()).willReturn(Stream.of(eventHandler));

        // when
        hotArticleService.handleEvent(event);

        // then
        verify(eventHandler, never()).handle(event);
        // 게시글 생성은 점수 수정은 호출하지 않음
        verify(hotArticleScoreUpdater).update(event, eventHandler);
    }
}
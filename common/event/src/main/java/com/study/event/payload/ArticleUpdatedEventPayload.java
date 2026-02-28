package com.study.event.payload;

import com.study.event.EventPayload;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArticleUpdatedEventPayload implements EventPayload {
    private Long articleId;
    private String title;
    private String boardId;
    private Long writerId;
    private LocalDateTime createAt;
    private LocalDateTime modifiedAt;
}
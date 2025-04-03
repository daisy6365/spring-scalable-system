package com.study.article.request;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class ArticleUpdateRequest {
    private String title;
    private String content;
    private Long writerId;
    private Long boardId;
}

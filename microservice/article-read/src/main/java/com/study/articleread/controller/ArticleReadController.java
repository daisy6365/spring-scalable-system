package com.study.articleread.controller;

import com.study.articleread.service.ArticleReadService;
import com.study.articleread.service.response.ArticleReadResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/articles")
public class ArticleReadController {
    private final ArticleReadService articleReadService;

    @GetMapping("{articleId}")
    public ArticleReadResponse read(@PathVariable("articleId") Long articleId) {
        return articleReadService.read(articleId);
    }
}


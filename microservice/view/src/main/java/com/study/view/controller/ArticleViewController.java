package com.study.view.controller;

import com.study.view.service.ArticleViewService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/article-views")
public class ArticleViewController {
    private final ArticleViewService articleViewService;

    @PostMapping("/articles/{articleId}/users/{userId}")
    public Long increase(@PathVariable("articleId") Long articleId, @PathVariable("userId") Long userId) {
        return articleViewService.increase(articleId, userId);
    }

    @GetMapping("/articles/{articleId}/count")
    public Long count(@PathVariable("articleId") Long articleId) {
        return articleViewService.count(articleId);
    }
}


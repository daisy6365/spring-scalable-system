package com.study.like.controller;

import com.study.like.response.ArticleLikeResponse;
import com.study.like.service.ArticleLikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/articles-likes")
public class ArticleLikeController {
    private final ArticleLikeService articleLikeService;

    @GetMapping("/articles/{articleId}/users/{userId}")
    public ArticleLikeResponse read(@PathVariable("articleId") Long articleId,
                                    @PathVariable("userId") Long userId) {
        return articleLikeService.read(articleId, userId);
    }

    @PostMapping("/articles/{articleId}/users/{userId}")
    public void like(@PathVariable("articleId") Long articleId,
                     @PathVariable("userId") Long userId) {
        articleLikeService.like(articleId, userId);
    }

    @DeleteMapping("/articles/{articleId}/users/{userId}")
    public void unlike(@PathVariable("articleId") Long articleId,
                       @PathVariable("userId") Long userId) {
        articleLikeService.unlike(articleId, userId);
    }
}

package com.study.articleread.controller;

import com.study.articleread.service.ArticleReadService;
import com.study.articleread.service.response.ArticleReadPageResponse;
import com.study.articleread.service.response.ArticleReadResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/articles")
public class ArticleReadController {
    private final ArticleReadService articleReadService;

    @GetMapping("{articleId}")
    public ArticleReadResponse read(@PathVariable("articleId") Long articleId) {
        return articleReadService.read(articleId);
    }

    @GetMapping
    public ArticleReadPageResponse readAll(@RequestParam("boardId")Long boardId,
                                           @RequestParam("page")Long page,
                                           @RequestParam("pageSize")Long pageSize) {
        return articleReadService.readAll(boardId, page, pageSize);
    }

    @GetMapping("/infinite-scroll")
    public List<ArticleReadResponse> readAllInfiniteScroll(@RequestParam("boardId")Long boardId,
                                                           @RequestParam(value = "lastArticleId", required = false)Long lastArticleId,
                                                           @RequestParam("pageSize")Long pageSize) {
        return articleReadService.readAllInfiniteScroll(boardId, lastArticleId, pageSize);
    }
}


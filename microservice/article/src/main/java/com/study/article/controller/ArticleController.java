package com.study.article.controller;

import com.study.article.request.ArticleCreateRequest;
import com.study.article.request.ArticleUpdateRequest;
import com.study.article.response.ArticleResponse;
import com.study.article.service.ArticleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class ArticleController {
    private final ArticleService articleService;

    @GetMapping("v1/articles/{articleID}")
    public ArticleResponse read(@PathVariable Long articleId){
        return articleService.read(articleId);
    }

    @PostMapping("v1/articles")
    public ArticleResponse create(@RequestBody ArticleCreateRequest request){
        return articleService.create(request);
    }

    @PutMapping("v1/articles/{articleId}")
    public ArticleResponse update(@PathVariable Long articleId, @RequestBody ArticleUpdateRequest request){
        return articleService.update(articleId, request);
    }

    @DeleteMapping("v1/articles/{articleId}")
    public void delete(@PathVariable Long articleId){
        articleService.delete(articleId);
    }
}

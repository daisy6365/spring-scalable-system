package com.study.hotarticle.controller;

import com.study.hotarticle.service.HotArticleService;
import com.study.hotarticle.service.response.HotArticleResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/hot-article")
public class HotArticleController {
    private final HotArticleService hotArticleService;

    @GetMapping("/articles/date/{dateStr}")
    public List<HotArticleResponse> readAll(@PathVariable("dateStr") String dateStr) {
        return hotArticleService.readAll(dateStr);
    }
}

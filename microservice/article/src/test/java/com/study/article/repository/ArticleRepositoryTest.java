package com.study.article.repository;

import com.study.article.entity.Article;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;


@Slf4j
@SpringBootTest
class ArticleRepositoryTest {
    @Autowired
    ArticleRepository articleRepository;

    @Test
    void findAllTest(){
        List<Article> articles = articleRepository.findAll(1L, 1499970L, 30L);
        log.info("articles.size = {}", articles.size());
        for (Article article : articles) {
            log.info("article = {}", article);
        }
    }

    @Test
    void countTest(){
        Long count = articleRepository.count(1L, 10000L);
        log.info("count = {}", count);
    }

    @Test
    void findAllInfiniteScroll(){
        List<Article> articles = articleRepository.findAllInfiniteScroll(1L, 30L);
        for (Article article : articles) {
            log.info("articleId = {}", article.getArticleId());
        }

        Long lastArticleId = articles.getLast().getArticleId();
        log.info("lastArticleId = {}", lastArticleId);

        List<Article> articles2 = articleRepository.findAllInfiniteScroll(1L, 30L, lastArticleId);
        for (Article article : articles2) {
            log.info("articleId = {}", article.getArticleId());
        }


    }

}
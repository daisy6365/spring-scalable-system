package com.study.hotarticle.service;

import com.study.hotarticle.repository.ArticleCommentCountRepository;
import com.study.hotarticle.repository.ArticleLikeCountRepository;
import com.study.hotarticle.repository.ArticleViewCountRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.random.RandomGenerator;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.BDDMockito.given;


// 단위테스트 화
@ExtendWith(MockitoExtension.class)
class HotArticleScoreCalculatorTest {
    @InjectMocks
    HotArticleScoreCalculator hotArticleScoreCalculator;
    @Mock
    ArticleCommentCountRepository articleCommentCountRepository;
    @Mock
    ArticleLikeCountRepository articleLikeCountRepository;
    @Mock
    ArticleViewCountRepository articleViewCountRepository;

    @Test
    void calculateTest(){
        // given
        Long articleId = 1L;
        long commentCount = RandomGenerator.getDefault().nextLong(100);
        long likeCount = RandomGenerator.getDefault().nextLong(100);
        long viewCount = RandomGenerator.getDefault().nextLong(100);
        given(articleCommentCountRepository.read(articleId)).willReturn(commentCount);
        given(articleLikeCountRepository.read(articleId)).willReturn(likeCount);
        given(articleViewCountRepository.read(articleId)).willReturn(viewCount);

        // when
        long score = hotArticleScoreCalculator.calculate(articleId);

        // then
        assertThat(score).isEqualTo(3 * likeCount + 2 * commentCount + 1 * viewCount);
    }

}
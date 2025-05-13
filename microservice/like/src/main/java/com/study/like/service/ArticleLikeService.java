package com.study.like.service;

import com.study.like.entity.ArticleLike;
import com.study.like.entity.ArticleLikeCount;
import com.study.like.repository.ArticleLikeCountRepository;
import com.study.like.repository.ArticleLikeRepository;
import com.study.like.response.ArticleLikeResponse;
import com.study.snowflake.Snowflake;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ArticleLikeService {
    private final Snowflake snowflake = new Snowflake();
    private final ArticleLikeRepository articleLikeRepository;
    private final ArticleLikeCountRepository articleLikeCountRepository;

    public ArticleLikeResponse read(Long articleId, Long userId) {
        return articleLikeRepository.findByArticleIdAndUserId(articleId, userId)
                .map(ArticleLikeResponse::from)
                .orElseThrow();
    }

    @Transactional
    public void likePessimisticLock1(Long articleId, Long userId) {
        articleLikeRepository.save(
                ArticleLike.create(
                        snowflake.nextId(),
                        articleId,
                        userId
                )
        );

        int result = articleLikeCountRepository.increase(articleId);
        if(result == 0){
            // insert된 데이터가 없음 -> 초기화해서 넣어줌
            // 트래픽이 순식간에 몰릴 수 있는 상황에는 유실될 수 있음
            // -> 게시글 생성 시점에 미리 0으로 초기화
            articleLikeCountRepository.save(
                    ArticleLikeCount.create(articleId, 1L)
            );
        }
    }

    @Transactional
    public void unlikePessimisticLock1(Long articleId, Long userId){
        articleLikeRepository.findByArticleIdAndUserId(articleId, userId)
                .ifPresent(articleLike -> {
                    articleLikeRepository.delete(articleLike);
                    articleLikeCountRepository.decrease(articleId);
                });
    }

    /**
     * select .. for update
     */
    @Transactional
    public void likePessimisticLock2(Long articleId, Long userId) {
        articleLikeRepository.save(
                ArticleLike.create(
                        snowflake.nextId(),
                        articleId,
                        userId
                )
        );

        articleLikeCountRepository.findLockedByArticleId(articleId)
                        .orElseGet(() -> ArticleLikeCount.create(articleId, 0L));
        articleLikeCountRepository.increase(articleId);
    }

    @Transactional
    public void unlikePessimisticLock2(Long articleId, Long userId){
        articleLikeRepository.findByArticleIdAndUserId(articleId, userId)
                .ifPresent(articleLike -> {
                    articleLikeRepository.delete(articleLike);
                    articleLikeCountRepository.findLockedByArticleId(articleId).orElseThrow();
                    articleLikeCountRepository.decrease(articleId);
                });

        articleLikeCountRepository.decrease(articleId);
    }


    @Transactional
    public void like(Long articleId, Long userId) {
        articleLikeRepository.save(
                ArticleLike.create(
                        snowflake.nextId(),
                        articleId,
                        userId
                )
        );
    }

    @Transactional
    public void unlike(Long articleId, Long userId) {
        articleLikeRepository.findByArticleIdAndUserId(articleId, userId)
                .ifPresent(articleLikeRepository::delete);
    }
}

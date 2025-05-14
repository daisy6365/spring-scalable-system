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

    /** [START] 비관적락 방법_1 **/
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
        /**
         * unlikePessimisticLock1 와 unlikePessimisticLock2에서 쿼리 수행 할 때,
         * JPA의 delete 메소드는 조회된 Entity가 이미 삭제된 상태여도 예외를 던지지 않음
         *
         * 즉, 동시에 수행하게 됨
         * [해결방법]
         * 직접 DELETE 쿼리를 정의해야 하고, 반환 값이 0이라면 예외발생 || 메세지를 통해 중복 수행 방지
         */
        articleLikeRepository.findByArticleIdAndUserId(articleId, userId)
                .ifPresent(articleLike -> {
                    int resultCount = articleLikeRepository.delete(articleLike.getArticleId());
                    if(resultCount == 0){
                        throw new IllegalStateException();
                    }
                    articleLikeCountRepository.decrease(articleId);
                });
    }
    /** [END] 비관적락 방법_1 **/


    /** [START] 비관적락 방법_2 **/
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

        ArticleLikeCount articleLikeCount = articleLikeCountRepository.findLockedByArticleId(articleId)
                // 데이터가 없다면 0으로 초기화
                .orElseGet(() -> ArticleLikeCount.create(articleId, 0L));
        articleLikeCount.increase();
        articleLikeCountRepository.save(articleLikeCount);
    }

    @Transactional
    public void unlikePessimisticLock2(Long articleId, Long userId){
        articleLikeRepository.findByArticleIdAndUserId(articleId, userId)
                .ifPresent(articleLike -> {
                    int resultCount = articleLikeRepository.delete(articleLike.getArticleId());
                    if(resultCount == 0){
                        throw new IllegalStateException();
                    }
                    ArticleLikeCount articleLikeCount = articleLikeCountRepository.findLockedByArticleId(articleId).orElseThrow();
                    articleLikeCount.decrease();
                });

        articleLikeCountRepository.decrease(articleId);
    }
    /** [END] 비관적락 방법_2 **/

    /** [START] 낙관적락 방법 **/
    @Transactional
    public void likeOptimisticLock(Long articleId, Long userId) {
        articleLikeRepository.save(
                ArticleLike.create(
                        snowflake.nextId(),
                        articleId,
                        userId
                )
        );

        ArticleLikeCount articleLikeCount = articleLikeCountRepository.findById(articleId)
                .orElseGet(() -> ArticleLikeCount.create(articleId, 0L));
        articleLikeCount.increase();
        articleLikeCountRepository.save(articleLikeCount);
    }

    @Transactional
    public void unlikeOptimisticLock(Long articleId, Long userId){
        articleLikeRepository.findByArticleIdAndUserId(articleId, userId)
                .ifPresent(articleLike -> {
                    articleLikeRepository.delete(articleLike);
                    ArticleLikeCount articleLikeCount = articleLikeCountRepository.findById(articleId).orElseThrow();
                    articleLikeCount.decrease();
                });
    }
    /** [END] 낙관적락 방법 **/

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

    public Long count(Long articleId){
        return articleLikeCountRepository.findById(articleId)
                .map(ArticleLikeCount::getLikeCount)
                .orElse(0L);
    }
}

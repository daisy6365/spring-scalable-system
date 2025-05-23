package com.study.view.processor;

import com.study.view.entity.ArticleViewCount;
import com.study.view.repository.ArticleViewCountBackUpRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ArticleViewCountBackupProcessor {
    public final ArticleViewCountBackUpRepository articleViewCountBackUpRepository;

    @Transactional
    public void backUp(Long articleId, Long viewCount) {
        int result = articleViewCountBackUpRepository.updateViewCount(articleId, viewCount);
        if(result == 0){
            articleViewCountBackUpRepository.findById(articleId)
                    .ifPresentOrElse(ignored -> {}, () -> {
                        // 없으면 해당 데이터 적재
                        articleViewCountBackUpRepository.save(ArticleViewCount.init(articleId, viewCount));
                    });
        }
    }
}

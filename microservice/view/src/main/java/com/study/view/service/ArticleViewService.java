package com.study.view.service;

import com.study.view.processor.ArticleViewCountBackupProcessor;
import com.study.view.repository.ArticleViewCountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ArticleViewService {
    private final ArticleViewCountRepository articleViewCountRepository;
    private final ArticleViewCountBackupProcessor articleViewCountBackupProcessor;
    private static final int BACK_UP_BATCH_SIZE = 100;

    public Long increase(Long articleId, Long userId) {
        Long count = articleViewCountRepository.increase(articleId);

        if(count % BACK_UP_BATCH_SIZE == 0) {
            articleViewCountBackupProcessor.backUp(articleId, userId);
        }
        return count;
    }

    public Long count(Long articleId) {
        return articleViewCountRepository.read(articleId);
    }

}

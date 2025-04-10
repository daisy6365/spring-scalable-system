package com.study.comment.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * page 번호 활성화에 필요한  count 계산
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PageLimitCalculator {
    /**
     * @param movablePageCount 특정 페이지 블록을 기준으로,
     *                         현재 페이지가 속한 블록의 **마지막 데이터 인덱스** 를 계산
     * ((page - 1) / movablePageCount) -> 현재 페이지가 몇번째 블록에 속하는지 계산
     * + 1 -> 페이지는 0부터 시작하지 않음. 무조건 +1
     * * pageSize * movablePageCount -> 현재 블록에서 표시한 전체 데이터 수
     * +1 -> 다음 limit를 구함
     */
    public static Long calculatePageLimit(Long page, Long pageSize, Long movablePageCount) {
        return (((page - 1) / movablePageCount) + 1) * pageSize * movablePageCount + 1;
    }
}

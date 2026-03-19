package com.study.article.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * page 번호 활성화에 필요한  count 계산
 * spring 내부 @Pageable 기능
 * -> 항상 전체 Count 쿼리 날림
 *
 * final : 상속 금지
 * PRIVATE 생성자 : 객체 생성 금지
 * == static 유틸 클래스와 표현이 같음 (오히려 더 명확)
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PageLimitCalculator {
    /**
     * @param page : 현재 보고 있는 페이지
     * @param pageSize : 한페이지에 보여줄 글 수
     * @param movablePageCount : 화면에서 보여줄 수 있는 페이지 블록 갯수
     * 특정 페이지 블록을 기준으로,
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

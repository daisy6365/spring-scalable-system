package com.study.outboxmessagerelay;

import lombok.Getter;

import java.util.List;
import java.util.stream.LongStream;

/**
 * 샤드를 Application에 균등하게 할당
 */
@Getter
public class AssignedShard {
    private List<Long> shards;

    /**
     *
     * @param appId : 지금 실행된 Application Id
     * @param appIds : Coordinator위에 실행되어있는 Application 목록
     * @param shardCount : 샤드의 갯수
     * @return
     */
    public static AssignedShard of(String appId, List<String> appIds, long shardCount) {
        AssignedShard assignedShard = new AssignedShard();
        assignedShard.shards = assign(appId, appIds, shardCount);
        return assignedShard;
    }

    private static List<Long> assign(String appId, List<String> appIds, long shardCount) {
        int appIndex = findAppIndex(appId, appIds);
        if (appIndex == -1) {
            // 할당할 샤드가 없음 -> 빈 리스트 반환
            return List.of();
        }

        // Application이 할당된 샤드의 범위를 구함
        long start = appIndex * shardCount / appIds.size();
        long end = (appIndex + 1) * shardCount / appIds.size() - 1;

        return LongStream.rangeClosed(start, end).boxed().toList();
    }

    /**
     * appIds은 실행된 Application 목록을 정렬상태로 가지고 있음
     * 그중에서 appId가 몇번째인지 가져옴
     */
    private static int findAppIndex(String appId, List<String> appIds) {
        for (int i = 0; i < appIds.size(); i++) {
            if (appIds.get(i).equals(appId)) {
                 return i;
            }
        }
        return -1;
    }
}

package com.study.outboxmessagerelay;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

class AssignedShardTest {

    @Test
    void ofTest(){
        // given
        Long shardCount = 64L;
        List<String> appList = List.of("appId1", "appId2", "appId3");

        // when
        AssignedShard assignedShard1 = AssignedShard.of(appList.get(0), appList, shardCount);
        AssignedShard assignedShard2 = AssignedShard.of(appList.get(1), appList, shardCount);
        AssignedShard assignedShard3 = AssignedShard.of(appList.get(2), appList, shardCount);
        AssignedShard assignedShard4 = AssignedShard.of("INVALID", appList, shardCount);

        // then
        List<Long> result = Stream.of(assignedShard1.getShards(), assignedShard2.getShards(), assignedShard3.getShards(), assignedShard4.getShards())
                .flatMap(List::stream)
                .toList();

        // 실행된 application에 할당된 샤드를 다 합치면 64개가 맞음
        assertThat(result).hasSize(shardCount.intValue());

        for (int i = 0; i < 64; i++) {
            assertThat(result.get(i)).isEqualTo(i);
        }

        // assignedShard4의 appId는 유효하지 않으므로 할당되 샤드가 없음 -> 빈 리스트 반환
        assertThat(assignedShard4.getShards()).isEmpty();
    }
}
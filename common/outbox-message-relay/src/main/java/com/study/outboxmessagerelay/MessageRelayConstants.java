package com.study.outboxmessagerelay;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MessageRelayConstants {
    // 임의로 샤드 갯수를 4개로 가정
    public static final int SHARD_COUNT = 4;

}

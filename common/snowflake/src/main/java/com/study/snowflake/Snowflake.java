package com.study.snowflake;

import java.util.random.RandomGenerator;

/**
 * 분산 시스템에서 고유한 ID를 생성하기 위해 설계된 방법
 * - 분산 환경에서도 중복없는 ID생성
 * - 시간에 따라 정렬 가능
 * - 초당 수백만 개의 ID 생성 가능
 * - 64비트 정수로 표현 가능
 * 시간 + 노드ID + 시퀀스 번호
 */
public class Snowflake {
	private static final int UNUSED_BITS = 1;
	private static final int EPOCH_BITS = 41;
	private static final int NODE_ID_BITS = 10;
	private static final int SEQUENCE_BITS = 12;

	private static final long maxNodeId = (1L << NODE_ID_BITS) - 1;
	private static final long maxSequence = (1L << SEQUENCE_BITS) - 1;

	private final long nodeId = RandomGenerator.getDefault().nextLong(maxNodeId + 1);
	// UTC = 2024-01-01T00:00:00Z
	private final long startTimeMillis = 1704067200000L;

	private long lastTimeMillis = startTimeMillis;
	private long sequence = 0L;

	// 멀티스레드 환경에서 동시성 문제를 막아줌
	public synchronized long nextId() {
		long currentTimeMillis = System.currentTimeMillis();

		// 시간 역행 방지
		if (currentTimeMillis < lastTimeMillis) {
			throw new IllegalStateException("Invalid Time");
		}

		// 같은 밀리초라면? 시퀀스 증가
		if (currentTimeMillis == lastTimeMillis) {
			sequence = (sequence + 1) & maxSequence;
			if (sequence == 0) {
				currentTimeMillis = waitNextMillis(currentTimeMillis);
			}
		} else {
			sequence = 0;
		}

		lastTimeMillis = currentTimeMillis;

		// 41비트의 시간 정보 | 10비트의 노드 ID | 12비트의 시퀀스 번호
		return ((currentTimeMillis - startTimeMillis) << (NODE_ID_BITS + SEQUENCE_BITS))
			| (nodeId << SEQUENCE_BITS)
			| sequence;
	}

	// 밀리초 대기 로직
	private long waitNextMillis(long currentTimestamp) {
		while (currentTimestamp <= lastTimeMillis) {
			currentTimestamp = System.currentTimeMillis();
		}
		return currentTimestamp;
	}
}

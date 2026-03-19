package com.study.snowflake;

import java.util.random.RandomGenerator;

/**
 * 분산 시스템에서 고유한 ID를 생성하기 위해 설계된 방법
 * long : 시간 + 노드ID + 시퀀스 번호
 * - 시간 : 정렬 가능
 * - 노드 ID : 분산 환경에서 충돌 제거
 * - 시퀀스 : 초당 수백만 개의 ID 생성 가능 -> 중복 방지
 * - 64비트 정수로 표현 가능
 * -> 대규모 시스템에 DB 병목 제거
 */
public class Snowflake {
	private static final int UNUSED_BITS = 1;
	private static final int EPOCH_BITS = 41;
	private static final int NODE_ID_BITS = 10;
	private static final int SEQUENCE_BITS = 12;

	private static final long maxNodeId = (1L << NODE_ID_BITS) - 1; // node의 크기 지정 < 1024
	private static final long maxSequence = (1L << SEQUENCE_BITS) - 1; // sequence의 크기 지정 < 4096

	// 독립적인 각 서버끼리 같은 Node Id를 공유
	// 겹칠 수도 있지만 학습용으로 nodeId관리는 하지 않음
	private final long nodeId = RandomGenerator.getDefault().nextLong(maxNodeId + 1);
	// UTC = 2024-01-01T00:00:00Z
	// 시간 값을 줄이고 비트 제한에 맞추기 위해 사용하는 기준 -> 모든 노드에서 동일하게 유지되어야 함
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

		// 비트연산으로 합침 : 41비트의 시간 | 10비트의 노드 ID | 12비트의 시퀀스 번호
		// (currentTimeMillis - startTimeMillis) : 특정 기준 시점 -> 시간 순서대로 정렬
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

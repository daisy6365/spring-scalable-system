package com.study.dataserializer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;

/**
 * Kafka Producer와 Consumer 사이에서 Event로 데이터를 통신할 때
 * 데이터를 직렬화/역직렬화 하는 과정이 존재
 *
 * @ 근데 spring-kafka 내부에서는 이미 직렬화/역직렬화 기능이 존재 @
 * spring-kafka는 설정/구조/메타데이터 포함 등의 기능이 강하에 결합된 상태
 * -> 대규모 시스템 강의에서는 spring-kafka 기능활용에 의존하는 것이 아닌,
 * -> kafka를 통한 독립적인 이벤트 모델을 직접 설계하고자 함
 */
@Slf4j
public final class DataSerializer {
    private static final ObjectMapper objectMapper = initialize();

    private static ObjectMapper initialize() {
        return new ObjectMapper()
                .registerModule(new JavaTimeModule())
                // 존재하지 않는 속성일 때 오류발생 끄기
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    /**
     * 역직렬화
     * String Type -> 특정 Class Type
     */
    public static <T> T deserialize(String data, Class<T> clazz) {
        try {
            return objectMapper.readValue(data, clazz);
        } catch (JsonProcessingException e) {
            log.error("[Deserializer.deserialize] data={}, clazz={}", data, clazz, e);
            return null;
        }
    }

    /**
     * 역직렬화
     * Object Type -> 특정 Class Type
     */
    public static <T>T deserialize(Object data, Class<T> clazz) {
        return objectMapper.convertValue(data, clazz);
    }

    /**
     * 직렬화
     * Object -> Json
     */
    public static String serialize(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            log.error("[Serializer.serialize] object={}", object, e);
            return null;
        }
    }
}

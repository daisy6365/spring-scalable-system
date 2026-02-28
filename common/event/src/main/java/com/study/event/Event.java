package com.study.event;

import com.study.dataserializer.DataSerializer;
import lombok.Getter;

/**
 * 다양하게 들어오는 데이터들을 하나의 Event로 정의
 * Producer와 Consumer에서 활용하여 통신
 */
@Getter
public class Event<T extends EventPayload> {
    private Long eventId; // 식별키
    private EventType type;
    private T payload; // 갖고있는 데이터 정보

    /**
     * 전송해야할 데이터들을 Event 객체에 담음
     */
    public static Event<EventPayload> of(Long eventId, EventType type, EventPayload payload) {
        Event<EventPayload> event = new Event<>();
        event.eventId = eventId;
        event.type = type;
        event.payload = payload;
        return event;
    }

    /**
     * Event를 Kafka로 전달할 때 Json으로 전달
     */
    public String toJson(){
        return DataSerializer.serialize(this);
    }

    /**
     * Json 데이터 -> Event 객체로 다시 변환
     */
    public static Event<EventPayload> fromJson(String json){
        EventRaw eventRaw = DataSerializer.deserialize(json, EventRaw.class);
        if(eventRaw == null){
            return null;
        }
        Event<EventPayload> event = new Event<>();
        event.eventId = eventRaw.getEventId();
        // string 으로 받은 데이터를 EventType을 통해 객체화
        event.type = EventType.from(eventRaw.getType());
        event.payload = DataSerializer.deserialize(eventRaw.getPayload(), event.type.getPayloadClass());
        return event;
    }

    /**
     * 데이터 타입에 따라 payload가 어떤 class type인지 달라지게 됨
     * 역직렬화 시점에는 해당 타입이 뭔지 모르기 때문에 초기에 받을 때는 string 문자열로 받음
     */
    @Getter
    private static class EventRaw {
        private Long eventId;
        private String type;
        private Object payload;
    }
}

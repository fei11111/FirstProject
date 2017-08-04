package com.fei.firstproject.event;

/**
 * Created by Administrator on 2017/7/31.
 */

public class AllEvent {

    EventType eventType;

    public EventType getEventType() {
        return eventType;
    }

    public AllEvent(EventType eventType) {
        this.eventType = eventType;
    }
}

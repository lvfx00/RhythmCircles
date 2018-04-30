package ru.nsu.fit.semenov.rhythmcircles;

import java.time.Duration;

enum EventType {TAP};

public interface GameEvent {
    EventType getEventType();

    Duration getBeginningTime();

    Duration getEventDuration();

    boolean finished(Duration time);
}

package ru.nsu.fit.semenov.rhythmcircles.events;

import java.time.Clock;

public interface GameEvent {

    void start(Clock clock);

    EventType getEventType();

    int getScores();

    boolean isFinished();
}

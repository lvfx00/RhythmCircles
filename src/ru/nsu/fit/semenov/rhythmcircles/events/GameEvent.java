package ru.nsu.fit.semenov.rhythmcircles.events;

import ru.nsu.fit.semenov.rhythmcircles.GameModel;

import java.time.Clock;

public interface GameEvent {
    void start(Clock clock, GameModel gameModel);

    EventType getEventType();

    int getScores();

    boolean isFinished();

    EventBounds getEventBounds();
}

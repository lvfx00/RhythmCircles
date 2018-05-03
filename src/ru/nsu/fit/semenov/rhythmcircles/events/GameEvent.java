package ru.nsu.fit.semenov.rhythmcircles.events;

import ru.nsu.fit.semenov.rhythmcircles.GamePresenter;

import java.time.Clock;
import java.util.Set;

public interface GameEvent {

    void start(Clock clock, Set<GamePresenter> presenters);

    EventType getEventType();

    int getScores();

    boolean isFinished();
}

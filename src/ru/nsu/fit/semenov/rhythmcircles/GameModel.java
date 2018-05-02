package ru.nsu.fit.semenov.rhythmcircles;

import ru.nsu.fit.semenov.rhythmcircles.events.EventStatus;
import ru.nsu.fit.semenov.rhythmcircles.events.GameEvent;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;

public class GameModel {
    public GameModel(RhythmMap rhythmMap) {
        this.rhythmMap = rhythmMap;
        scoreSum = 0;
    }

    public void start() {
        startingTime = clock.instant();
    }

    public void update() {
        Duration currTime = Duration.between(startingTime, clock.instant());

        // add new events from queue
        while (rhythmMap.available(currTime)) {
            GameEvent gameEvent = rhythmMap.getNextEvent();
            gameEvent.start(clock);

            for (GamePresenter presenter : registeredPresenters) {
                presenter.addEventView(gameEvent);
            }

            eventsOnScreen.put(gameEvent, Boolean.TRUE);
        }

        // remove outdated views
        for (GameEvent gameEvent : eventsOnScreen.keySet()) {
            if (EventStatus.FINISHED == gameEvent.getStatus()) {
                for (GamePresenter presenter : registeredPresenters) {
                    scoreSum += gameEvent.getScores();
                    presenter.showScores(gameEvent, gameEvent.getScores());
                    presenter.removeEventView(gameEvent);
                }
                eventsOnScreen.remove(gameEvent);
            }
        }

    }

    public void registerPresenter(GamePresenter gp) {
        registeredPresenters.add(gp);
    }

    private final Clock clock = Clock.systemUTC();
    private Instant startingTime;

    private HashSet<GamePresenter> registeredPresenters = new HashSet<>();

    private final RhythmMap rhythmMap;

    private ConcurrentHashMap<GameEvent, Boolean> eventsOnScreen = new ConcurrentHashMap<>();

    private int scoreSum;


}

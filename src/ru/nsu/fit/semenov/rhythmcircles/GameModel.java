package ru.nsu.fit.semenov.rhythmcircles;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.HashSet;

public class GameModel {
    public GameModel(RhythmMap rm) {
        rhythmMap = rm;
    }

    public void start() {
        startingTime = clock.instant();
    }

    public void update() {
        Duration currTime = Duration.between(startingTime, clock.instant());

        // add new events from queue
        while (rhythmMap.hasNext(currTime)) {
            System.out.println("axaxaxa");
            GameEvent gameEvent = rhythmMap.getNext();
            for (GamePresenter presenter : registeredPresenters) {
                presenter.addEventView(gameEvent);
            }
            eventsOnScreen.add(gameEvent);
        }

        // remove outdated views
        for (GameEvent gameEvent : eventsOnScreen) {
            if (gameEvent.finished(currTime)) {
                for (GamePresenter presenter : registeredPresenters) {
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
    private HashSet<GameEvent> eventsOnScreen = new HashSet<>();
}

package ru.nsu.fit.semenov.rhythmcircles;

import org.jetbrains.annotations.NotNull;
import ru.nsu.fit.semenov.rhythmcircles.events.GameEvent;
import ru.nsu.fit.semenov.rhythmcircles.events.SlideEvent;
import ru.nsu.fit.semenov.rhythmcircles.events.TapEvent;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class MyGameModel implements GameModel {
    public MyGameModel(RhythmMap rhythmMap) {
        this.rhythmMap = rhythmMap;
        scoreSum = 0;
        started = false;
    }

    @Override
    public void start() {
        started = true;
        startingTime = clock.instant();
    }

    @Override
    public void update() {
        // run submitted tasks
        for(Runnable r : taskQueue) {
            r.run();
            // is it good way ????777
            taskQueue.remove(r);
        }

        Duration currTime = Duration.between(startingTime, clock.instant());

        // add new events from queue
        while (rhythmMap.available(currTime)) {
            GameEvent gameEvent = rhythmMap.getNextEvent();
            gameEvent.start(clock, this);

            for (GamePresenter presenter : registeredPresenters.keySet()) {
                switch (gameEvent.getEventType()) {
                    case TAP:
                        presenter.addTapEventView((TapEvent) gameEvent);
                        break;
                    case SLIDE:
                        presenter.addSlideEventView((SlideEvent) gameEvent);
                        break;
                }
            }

            eventsOnScreen.put(gameEvent, Boolean.TRUE);
        }

        // remove outdated views
        for (GameEvent gameEvent : eventsOnScreen.keySet()) {
            if (gameEvent.isFinished()) {
                for (GamePresenter presenter : registeredPresenters.keySet()) {
                    scoreSum += gameEvent.getScores();
                    switch (gameEvent.getEventType()) {
                        case TAP:
                            presenter.removeTapEventView((TapEvent) gameEvent);
                            break;
                        case SLIDE:
                            presenter.removeSlideEventView((SlideEvent) gameEvent);
                            break;
                    }
                }
                eventsOnScreen.remove(gameEvent);
            }
        }

    }

    @Override
    public void registerPresenter(GamePresenter gp) {
        if(!started) {
            registeredPresenters.put(gp, true);
        }
    }

    @Override
    public void submitTask(@NotNull Runnable r) {
        taskQueue.add(r);
    }

    @Override
    public Set<GamePresenter> getPresentersView() {
        return registeredPresenters.keySet();
    }

    private final Clock clock = Clock.systemUTC();
    private Instant startingTime;

    private ConcurrentHashMap<GamePresenter, Boolean> registeredPresenters = new ConcurrentHashMap<>();
    private ConcurrentHashMap<GameEvent, Boolean> eventsOnScreen = new ConcurrentHashMap<>();

    private ConcurrentLinkedQueue<Runnable> taskQueue = new ConcurrentLinkedQueue<>();

    private final RhythmMap rhythmMap;

    private int scoreSum;

    private boolean started;
}

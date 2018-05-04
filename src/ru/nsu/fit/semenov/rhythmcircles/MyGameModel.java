package ru.nsu.fit.semenov.rhythmcircles;

import javafx.geometry.Point2D;
import org.jetbrains.annotations.NotNull;
import ru.nsu.fit.semenov.rhythmcircles.events.GameEvent;
import ru.nsu.fit.semenov.rhythmcircles.events.SlideEvent;
import ru.nsu.fit.semenov.rhythmcircles.events.TapEvent;
import ru.nsu.fit.semenov.rhythmcircles.views.ViewParams;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;

import static ru.nsu.fit.semenov.rhythmcircles.MainApplication.SCREEN_HEIGHT;
import static ru.nsu.fit.semenov.rhythmcircles.MainApplication.SCREEN_WIDTH;

public class MyGameModel implements GameModel {
    public static final int CIRCLE_RADIUS = 70;

    // without rhythm map. Random generation
    public MyGameModel() {
        scoreSum = 0;
        started = false;
        hasRhythmMap = false;
        hasPresenter = false;

        nextCreation = Duration.ZERO;
    }

    public MyGameModel(@NotNull RhythmMap rhythmMap) {
        scoreSum = 0;
        started = false;
        hasRhythmMap = true;
        this.rhythmMap = rhythmMap;
    }

    @Override
    public void start() {
        started = true;
        startingTime = clock.instant();
    }

    @Override
    public void update() {
        // run submitted tasks
        for (Consumer<GamePresenter> r : taskQueue) {
            r.accept(presenter);

            // is it a good way ????777
            taskQueue.remove(r);
        }

        Duration currTime = Duration.between(startingTime, clock.instant());

        if (hasRhythmMap) {
            // add new events from queue
            while (rhythmMap.available(currTime)) {
                GameEvent gameEvent = rhythmMap.getNextEvent();
                gameEvent.start(clock, this);

                switch (gameEvent.getEventType()) {
                    case TAP:
                        presenter.addTapEventView((TapEvent) gameEvent);
                        break;
                    case SLIDE:
                        presenter.addSlideEventView((SlideEvent) gameEvent);
                        break;
                }
                eventsOnScreen.put(gameEvent, Boolean.TRUE);
            }

        } else {
            // generate
            if (nextCreation.compareTo(currTime) < 0) {
                int randomNum = ThreadLocalRandom.current().nextInt(0, 2);
                switch (randomNum) {
                    case 0:
                        double x = ThreadLocalRandom.current().nextDouble(ViewParams.RADIUS, SCREEN_WIDTH - ViewParams.RADIUS);
                        double y = ThreadLocalRandom.current().nextDouble(ViewParams.RADIUS, SCREEN_HEIGHT - ViewParams.RADIUS);
                        Point2D creationPoint = new Point2D(x, y);

                        boolean fits = true;

                        for (GameEvent gameEvent : eventsOnScreen.keySet()) {
                            switch (gameEvent.getEventType()) {
                                case TAP:
                                    if (creationPoint.distance(((TapEvent) gameEvent).getX(),
                                            ((TapEvent) gameEvent).getY()) < CIRCLE_RADIUS * 2) {
                                        fits = false;
                                    }
                                    break;
                                case SLIDE:
                                    break;
                            }
                        }

                        if(fits) {

                        }
                        break;

                    case 1:
                        break;
                }
            }
        }

        // remove outdated views
        for (GameEvent gameEvent : eventsOnScreen.keySet()) {
            if (gameEvent.isFinished()) {
                scoreSum += gameEvent.getScores();
                switch (gameEvent.getEventType()) {
                    case TAP:
                        presenter.removeTapEventView((TapEvent) gameEvent);
                        break;
                    case SLIDE:
                        presenter.removeSlideEventView((SlideEvent) gameEvent);
                        break;
                }
                eventsOnScreen.remove(gameEvent);
            }
        }
    }

    @Override
    public void registerPresenter(GamePresenter gp) {
        if (!hasPresenter) {
            presenter = gp;
        }
    }

    @Override
    public void submitTask(Consumer<GamePresenter> cons) {
        taskQueue.add(cons);
    }

    private final Clock clock = Clock.systemUTC();
    private Instant startingTime;
    private boolean started;

    private boolean hasPresenter;
    private GamePresenter presenter;
    private ConcurrentHashMap<GameEvent, Boolean> eventsOnScreen = new ConcurrentHashMap<>();

    private ConcurrentLinkedQueue<Consumer<GamePresenter>> taskQueue = new ConcurrentLinkedQueue<>();

    // time for next creation
    private Duration nextCreation;

    private RhythmMap rhythmMap;
    private boolean hasRhythmMap;

    private int scoreSum;
}

package ru.nsu.fit.semenov.rhythmcircles;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.nsu.fit.semenov.rhythmcircles.events.*;
import ru.nsu.fit.semenov.rhythmcircles.views.ViewParams;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;

import static ru.nsu.fit.semenov.rhythmcircles.MainApplication.SCREEN_HEIGHT;
import static ru.nsu.fit.semenov.rhythmcircles.MainApplication.SCREEN_WIDTH;

public class MyGameModel implements GameModel {
    private static final double MIN_SLIDE_LENGTH = 250;
    private static final double MAX_SLIDE_LENGTH = 350;

    public static final double CIRCLE_RADIUS = 70;

    private static final Duration UPDATE_INTERVAL = Duration.ofSeconds(2);
    private static final Duration FUTURE = Duration.ofSeconds(2);


    public MyGameModel() {
        scoreSum = 0;
        started = false;
    }

    @Override
    public void start(@NotNull GamePresenter gp, @NotNull TimeMap timeMap) {
        started = true;
        startingTime = clock.instant();
        presenter = gp;
        this.timeMap = timeMap;
        lastEventsUpdate = Instant.MIN;
    }

    @Override
    public void update() {
        Instant currTime = clock.instant();
        Duration elapsed = Duration.between(startingTime, currTime);

        // remove outdated views
        ArrayList<GameEvent> eventsToRemove = new ArrayList<>();

        for (GameEvent gameEvent : eventsOnScreen) {
            if (gameEvent.isFinished()) {
                switch (gameEvent.getEventType()) {
                    case TAP:
                        presenter.remove((TapEvent) gameEvent);
                        break;
                    case SLIDE:
                        presenter.remove((SlideEvent) gameEvent);
                        break;
                }
                scoreSum += gameEvent.getScores();

                eventsToRemove.add(gameEvent);
            }
        }
        eventsOnScreen.removeAll(eventsToRemove);

        // add new events to queue
        if (Duration.between(lastEventsUpdate, currTime).compareTo(UPDATE_INTERVAL) > 0) {
            while (timeMap.hasNext(elapsed.plus(FUTURE))) {
                Duration beatTime = timeMap.getNext();
                int rand = ThreadLocalRandom.current().nextInt(1, 8);
                // tap event
                if (rand < 4) {
                    TapEvent newTapEvent = generateTapEvent();
                    eventsOnScreen.add(newTapEvent);
                    presenter.add(newTapEvent, ThreadLocalRandom.current().nextInt(1, 5));
                    plannedEvents.add(new Pair<>(newTapEvent, beatTime.minus(TapEvent.BEFORE_TAP)));
                }
                // one-diraction sliding
                else if (rand < 6) {
                    Duration slideDuration = timeMap.getNext().minus(beatTime);
                    SlideEvent newSlideEvent = generateSlideEvent(slideDuration, null);
                    eventsOnScreen.add(newSlideEvent);
                    presenter.add(newSlideEvent, ThreadLocalRandom.current().nextInt(1, 5));
                    plannedEvents.add(new Pair<>(newSlideEvent, beatTime.minus(TapEvent.BEFORE_TAP)));

                }
                // two-direction sliding
                else {
                    Duration slideForwardDuration = timeMap.getNext().minus(beatTime);
                    Duration slideBackwardDuration = timeMap.getNext().minus(slideForwardDuration).minus(beatTime);

                    SlideEvent newSlideEvent = generateSlideEvent(slideForwardDuration, slideBackwardDuration);
                    eventsOnScreen.add(newSlideEvent);
                    presenter.add(newSlideEvent, ThreadLocalRandom.current().nextInt(1, 5));
                    plannedEvents.add(new Pair<>(newSlideEvent, beatTime.minus(TapEvent.BEFORE_TAP)));
                }
            }
        }


        ArrayList<Pair<GameEvent, Duration>> toRemove = new ArrayList<>();
        // check if has planned events to start
        for (Pair<GameEvent, Duration> event : plannedEvents) {
            if (elapsed.compareTo(event.right) > 0) {
                event.left.start(clock, this);
                switch (event.left.getEventType()) {
                    case TAP:
                        presenter.start((TapEvent) event.left);
                        break;
                    case SLIDE:
                        presenter.start((SlideEvent) event.left);
                        break;
                }
                toRemove.add(event);
            }
        }
        plannedEvents.removeAll(toRemove);

        ArrayList<Consumer<GamePresenter>> tasksToRemove = new ArrayList<>();
        // update view details
        for (Consumer<GamePresenter> r : eventTasksQueue) {
            r.accept(presenter);
            tasksToRemove.add(r);
        }
        eventTasksQueue.removeAll(tasksToRemove);
    }


    private TapEvent generateTapEvent() {
        while (true) {
            boolean fits = true;

            double x = ThreadLocalRandom.current().nextDouble(ViewParams.RADIUS, SCREEN_WIDTH - ViewParams.RADIUS);
            double y = ThreadLocalRandom.current().nextDouble(ViewParams.RADIUS, SCREEN_HEIGHT - ViewParams.RADIUS);

            EventBounds newEventBounds = TapEvent.calcEventBounds(x, y);
            // check intersections with events on screen
            for (GameEvent gameEvent : eventsOnScreen) {
                if (newEventBounds.intersects(gameEvent.getEventBounds())) {
                    fits = false;
                }
            }

            if (fits) {
                return new TapEvent(x, y);
            }
        }
    }


    private SlideEvent generateSlideEvent(@NotNull Duration forward, @Nullable Duration backward) {
        while (true) {
            boolean fits = true;

            double x1 = ThreadLocalRandom.current().nextDouble(ViewParams.RADIUS, SCREEN_WIDTH - ViewParams.RADIUS);
            double y1 = ThreadLocalRandom.current().nextDouble(ViewParams.RADIUS, SCREEN_HEIGHT - ViewParams.RADIUS);

            double x2 = ThreadLocalRandom.current().nextDouble(ViewParams.RADIUS, SCREEN_WIDTH - ViewParams.RADIUS);
            double y2 = ThreadLocalRandom.current().nextDouble(ViewParams.RADIUS, SCREEN_HEIGHT - ViewParams.RADIUS);

            // requirements
            double length = Math.sqrt(Math.pow(x2 - x1, 2.0) + Math.pow(y2 - y1, 2.0));
            if (length < MIN_SLIDE_LENGTH || length > MAX_SLIDE_LENGTH) {
                continue;
            }

            EventBounds newEventBounds = SlideEvent.calcEventBounds(x1, y1, x2, y2);
            for (GameEvent gameEvent : eventsOnScreen) {
                if (newEventBounds.intersects(gameEvent.getEventBounds())) {
                    fits = false;
                }
            }

            if (fits) {
                return new SlideEvent(x1, y1, x2, y2, forward, backward);
            }
        }
    }

    @Override
    public void submitEventTask(Consumer<GamePresenter> cons) {
        eventTasksQueue.add(cons);
    }


    private final Clock clock = Clock.systemUTC();
    private Instant startingTime;

    private boolean started;
    private boolean finished;
    private int scoreSum;

    private GamePresenter presenter;
    private TimeMap timeMap;

    private ArrayList<Pair<GameEvent, Duration>> plannedEvents = new ArrayList<>();
    private HashSet<GameEvent> eventsOnScreen = new HashSet<>();

    private ConcurrentLinkedQueue<Consumer<GamePresenter>> eventTasksQueue = new ConcurrentLinkedQueue<>();

    private Instant lastEventsUpdate;

    class Pair<@NotNull L, @NotNull R> {

        private final L left;
        private final R right;

        Pair(L left, R right) {
            this.left = left;
            this.right = right;
        }

        @NotNull
        L getLeft() {
            return left;
        }

        @NotNull
        R getRight() {
            return right;
        }

        @Override
        public int hashCode() {
            return left.hashCode() ^ right.hashCode();
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Pair)) return false;
            Pair pairo = (Pair) o;
            return this.left.equals(pairo.getLeft()) &&
                    this.right.equals(pairo.getRight());
        }
    }
}

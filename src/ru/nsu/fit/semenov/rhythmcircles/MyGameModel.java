package ru.nsu.fit.semenov.rhythmcircles;

import org.jetbrains.annotations.NotNull;
import ru.nsu.fit.semenov.rhythmcircles.events.*;
import ru.nsu.fit.semenov.rhythmcircles.views.ViewParams;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;

import static ru.nsu.fit.semenov.rhythmcircles.MainApplication.SCREEN_HEIGHT;
import static ru.nsu.fit.semenov.rhythmcircles.MainApplication.SCREEN_WIDTH;
import static ru.nsu.fit.semenov.rhythmcircles.events.EventType.SLIDE;
import static ru.nsu.fit.semenov.rhythmcircles.events.EventType.TAP;

public class MyGameModel implements GameModel {
    private static final double MIN_SLIDE_LENGTH = 50;
    private static final double MAX_SLIDE_LENGTH = 250;

    public static final double CIRCLE_RADIUS = 70;

    private static final Duration FUTURE = Duration.ofSeconds(5);


    public MyGameModel() {
        scoreSum = 0;
        started = false;
        this.timeline = timeline;
    }

    @Override
    public void start(@NotNull GamePresenter gp, @NotNull Timeline timeline) {
        started = true;
        startingTime = clock.instant();
        presenter = gp;
    }

    @Override
    public void update() {
        Duration currTime = Duration.between(startingTime, clock.instant());

        // remove outdated views
        for (GameEvent gameEvent : eventsOnScreen.keySet()) {
            if (gameEvent.isFinished()) {
                switch (gameEvent.getEventType()) {
                    case TAP:
                        presenter.removeTapEventView((TapEvent) gameEvent);
                        break;
                    case SLIDE:
                        presenter.removeSlideEventView((SlideEvent) gameEvent);
                        break;
                }
                scoreSum += gameEvent.getScores();
                eventsOnScreen.remove(gameEvent);
            }
        }

        // create Event
        while (timeline.hasNextInFuture(currTime, FUTURE)) {

            EventType eventType = (ThreadLocalRandom.current().nextInt() % 2 == 1) ? TAP : SLIDE;


        }

        // check if has planned events to start
        for (Pair<GameEvent, Duration> event : plannedEvents) {
            if (currTime.compareTo(event.right) > 0) {
                event.left.start(clock, this);
                switch (event.left.getEventType()) {
                    case TAP:
                        presenter.startTapEvent((TapEvent) event.left);
                        break;
                    case SLIDE:
                        presenter.startSlideEvent((SlideEvent) event.left);
                        break;
                }
            }
            plannedEvents.remove(event);
        }

        // update view details
        for (Consumer<GamePresenter> r : eventTasksQueue) {
            r.accept(presenter);
            eventTasksQueue.remove(r);
        }
    }


    private TapEvent generateTapEvent() {
        while (true) {
            boolean fits = true;

            double x = ThreadLocalRandom.current().nextDouble(ViewParams.RADIUS, SCREEN_WIDTH - ViewParams.RADIUS);
            double y = ThreadLocalRandom.current().nextDouble(ViewParams.RADIUS, SCREEN_HEIGHT - ViewParams.RADIUS);

            EventBounds newEventBounds = TapEvent.calcEventBounds(x, y);
            // check intersections with events on screen
            for (GameEvent gameEvent : eventsOnScreen.keySet()) {
                if (newEventBounds.intersects(gameEvent.getEventBounds())) {
                    fits = false;
                }
            }

            if (fits) {
                return new TapEvent(x, y);
            }
        }
    }


    private SlideEvent generateSlideEvent(Duration eventDuration) {
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
            for (GameEvent gameEvent : eventsOnScreen.keySet()) {
                if (newEventBounds.intersects(gameEvent.getEventBounds())) {
                    fits = false;
                }
            }

            if (fits) {
                return new SlideEvent(x1, y1, x2, y2, eventDuration);
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
    private Timeline timeline;

    private Queue<Pair<GameEvent, Duration>> plannedEvents = new LinkedList<>();
    private ConcurrentHashMap<GameEvent, Boolean> eventsOnScreen = new ConcurrentHashMap<>();

    private ConcurrentLinkedQueue<Consumer<GamePresenter>> eventTasksQueue = new ConcurrentLinkedQueue<>();

    private class Pair<@NotNull L, @NotNull R> {

        private final L left;
        private final R right;

        public Pair(L left, R right) {
            this.left = left;
            this.right = right;
        }

        @NotNull
        public L getLeft() {
            return left;
        }

        @NotNull
        public R getRight() {
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

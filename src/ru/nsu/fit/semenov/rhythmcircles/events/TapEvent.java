package ru.nsu.fit.semenov.rhythmcircles.events;

import javafx.geometry.Bounds;
import javafx.scene.shape.Circle;
import ru.nsu.fit.semenov.rhythmcircles.GameModel;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;

import static ru.nsu.fit.semenov.rhythmcircles.MyGameModel.CIRCLE_RADIUS;
import static ru.nsu.fit.semenov.rhythmcircles.events.EventType.TAP;

public class TapEvent implements GameEvent {
    private enum TapEventStatus {
        NOT_STARTED,
        IN_PROGRESS,
        FINISHED
    }

    public static final Duration DURATION = Duration.ofMillis(1350);
    public static final Duration BEFORE_TAP = Duration.ofMillis(925);

    public static final Duration TAP_TOO_EARLY = Duration.ofMillis(500); // 0 scores
    public static final Duration TAP_REGULAR = Duration.ofMillis(300); // 100 scores
    public static final Duration TAP_PERFECT = Duration.ofMillis(250); // 300 scores
    public static final Duration TAP_TOO_LATE = Duration.ofMillis(300); // 0 scores

    public TapEvent(double x, double y) {
        this.x = x;
        this.y = y;
        scores = 0;
        eventStatus = TapEventStatus.NOT_STARTED;

        // set event bounds
        eventBounds = calcEventBounds(x, y);
    }

    @Override
    public void start(Clock clock, GameModel gameModel) {
        // set clocks for this event
        this.clock = clock;
        beginningTime = clock.instant();
        eventStatus = TapEventStatus.IN_PROGRESS;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public void tap() {
        if (eventStatus == TapEventStatus.IN_PROGRESS) {

            if (Duration.between(beginningTime, clock.instant()).compareTo(TAP_TOO_EARLY) < 0) {
                scores = 0;

            } else if (Duration.between(beginningTime, clock.instant()).
                    compareTo(TAP_TOO_EARLY.plus(TAP_REGULAR)) < 0) {
                scores = 100;

            } else if (Duration.between(beginningTime, clock.instant()).
                    compareTo(TAP_TOO_EARLY.plus(TAP_REGULAR).plus(TAP_PERFECT)) < 0) {
                scores = 300;

            } else if (Duration.between(beginningTime, clock.instant()).
                    compareTo(TAP_TOO_EARLY.plus(TAP_REGULAR).plus(TAP_PERFECT).plus(TAP_TOO_LATE)) < 0) {
                scores = 50;
            }

            eventStatus = TapEventStatus.FINISHED;
        }
    }

    public static EventBounds calcEventBounds(double x, double y) {
        Circle startCircle = new Circle(CIRCLE_RADIUS);
        startCircle.setCenterX(x);
        startCircle.setCenterY(y);

        Bounds bounds = startCircle.getBoundsInLocal();

        return  new EventBounds(bounds);
    }

    @Override
    public EventType getEventType() {
        return TAP;
    }

    @Override
    public EventBounds getEventBounds() {
        return eventBounds;
    }

    @Override
    public boolean isFinished() {
        if (TapEventStatus.IN_PROGRESS == eventStatus &&
                Duration.between(beginningTime, clock.instant()).compareTo(DURATION) > 0) {
            eventStatus = TapEventStatus.FINISHED;
        }
        return TapEventStatus.FINISHED == eventStatus;
    }

    @Override
    public int getScores() {
        if(TapEventStatus.FINISHED == eventStatus) {
            return scores;
        }
        return  0;
    }

    private final double x;
    private final double y;

    private final EventBounds eventBounds;

    private int scores;

    private TapEventStatus eventStatus;

    private Clock clock;
    private Instant beginningTime;

}

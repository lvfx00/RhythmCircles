package ru.nsu.fit.semenov.rhythmcircles.events;

import ru.nsu.fit.semenov.rhythmcircles.GamePresenter;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Set;

import static ru.nsu.fit.semenov.rhythmcircles.events.EventType.TAP;

enum TapEventStatus {
    NOT_STARTED,
    IN_PROGRESS,
    FINISHED
}

public class TapEvent implements GameEvent {
    public static final Duration DURATION = Duration.ofMillis(2000);

    public static final Duration TOO_EARLY = Duration.ofMillis(750); // 0 scores
    public static final Duration REGULAR = Duration.ofMillis(500); // 100 scores
    public static final Duration PERFECT = Duration.ofMillis(250); // 300 scores
    public static final Duration TOO_LATE = Duration.ofMillis(500); // 0 scores

    public TapEvent(double x, double y) {
        this.x = x;
        this.y = y;
        scores = 0;
        eventStatus = TapEventStatus.NOT_STARTED;
    }

    @Override
    public void start(Clock clock, Set<GamePresenter> presenters) {
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

            if (Duration.between(beginningTime, clock.instant()).compareTo(TOO_EARLY) < 0) {
                scores = 0;

            } else if (Duration.between(beginningTime, clock.instant()).
                    compareTo(TOO_EARLY.plus(REGULAR)) < 0) {
                scores = 100;

            } else if (Duration.between(beginningTime, clock.instant()).
                    compareTo(TOO_EARLY.plus(REGULAR).plus(PERFECT)) < 0) {
                scores = 300;

            } else if (Duration.between(beginningTime, clock.instant()).
                    compareTo(TOO_EARLY.plus(REGULAR).plus(PERFECT).plus(TOO_LATE)) < 0) {
                scores = 50;
            }

            eventStatus = TapEventStatus.FINISHED;
        }
    }

    @Override
    public EventType getEventType() {
        return TAP;
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

    private int scores;

    private TapEventStatus eventStatus;

    private Clock clock;
    private Instant beginningTime;

}

package ru.nsu.fit.semenov.rhythmcircles.events;

import ru.nsu.fit.semenov.rhythmcircles.GameModel;
import ru.nsu.fit.semenov.rhythmcircles.GamePresenter;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.*;

import static ru.nsu.fit.semenov.rhythmcircles.events.EventType.SLIDE;

public class SlideEvent implements GameEvent {
    private enum SlideEventStatus {
        NOT_STARTED,
        AWAITING_TAP,
        SLIDING,
        AWAITING_RELEASE,
        FINISHED,
    }

    private static final Duration TOO_EARLY = Duration.ofMillis(750);
    private static final Duration REGULAR = Duration.ofMillis(500);
    private static final Duration PERFECT = Duration.ofMillis(250);

    private static final Duration RELEASE_DURATION = Duration.ofMillis(250);

    private static final Duration BEFORE_SLIDING = Duration.ofMillis(1500);

    public SlideEvent(double x1, double y1, double x2, double y2, Duration slideDuration) {
        this.startX = x1;
        this.startY = y1;
        this.finishX = x2;
        this.finishY = y2;
        this.slideDuration = slideDuration;
        eventStatus = SlideEventStatus.NOT_STARTED;
        scores = 0;
    }

    @Override
    public void start(Clock clock, GameModel gameModel) {
        // set clocks for this event
        this.clock = clock;
        beginningTime = clock.instant();
        this.gameModel = gameModel;

        eventStatus = SlideEventStatus.AWAITING_TAP;

        executor = Executors.newSingleThreadScheduledExecutor();

        executor.schedule(this::startSliding, BEFORE_SLIDING.toMillis(), TimeUnit.MILLISECONDS);

        executor.schedule(() -> {
                    eventStatus = SlideEventStatus.AWAITING_RELEASE;
                },
                BEFORE_SLIDING.plus(slideDuration).minus(RELEASE_DURATION).toMillis(), TimeUnit.MILLISECONDS);

        executor.schedule(() -> {
                    eventStatus = SlideEventStatus.FINISHED;
                },
                BEFORE_SLIDING.plus(slideDuration).plus(RELEASE_DURATION).toMillis(), TimeUnit.MILLISECONDS);

    }

    public void tap() {
        if (eventStatus == SlideEventStatus.AWAITING_TAP) {

            if (Duration.between(beginningTime, clock.instant()).compareTo(TOO_EARLY) < 0) {
                scores += 0;

            } else if (Duration.between(beginningTime, clock.instant()).
                    compareTo(TOO_EARLY.plus(REGULAR)) < 0) {
                scores += 50;
                startSliding();

            } else if (Duration.between(beginningTime, clock.instant()).
                    compareTo(TOO_EARLY.plus(REGULAR).plus(PERFECT)) < 0) {
                scores += 150;
                startSliding();
            }
        }
    }

    public void release() {
        if (eventStatus == SlideEventStatus.AWAITING_RELEASE) {
            scores += 150;
            eventStatus = SlideEventStatus.FINISHED;
        }
    }

    public double getStartX() {
        return startX;
    }

    public double getStartY() {
        return startY;
    }

    public double getFinishX() {
        return finishX;
    }

    public double getFinishY() {
        return finishY;
    }

    @Override
    public EventType getEventType() {
        return SLIDE;
    }

    @Override
    public boolean isFinished() {
        return SlideEventStatus.FINISHED == eventStatus;
    }

    @Override
    public int getScores() {
        if (eventStatus == SlideEventStatus.FINISHED) {
            if (!executor.isShutdown()) {
                executor.shutdownNow();
            }
            return scores;
        }
        return 0;
    }

    public Duration getSlideDuration() {
        return slideDuration;
    }

    private void startSliding() {
        if (SlideEventStatus.AWAITING_TAP == eventStatus) {
            eventStatus = SlideEventStatus.SLIDING;
            gameModel.submitTask((GamePresenter presenter) -> {
                    presenter.startSliding(this);
            });
        }
    }

    private final double startX;
    private final double startY;
    private final double finishX;
    private final double finishY;

    private final Duration slideDuration;

    private SlideEventStatus eventStatus;
    private int scores;

    private ScheduledExecutorService executor;
    private GameModel gameModel;

    private Clock clock;
    private Instant beginningTime;

}

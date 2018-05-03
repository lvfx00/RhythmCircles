package ru.nsu.fit.semenov.rhythmcircles.events;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import ru.nsu.fit.semenov.rhythmcircles.GamePresenter;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.*;

import static ru.nsu.fit.semenov.rhythmcircles.events.EventType.SLIDE;

enum SlideEventStatus {
    NOT_STARTED,
    AWAITING_TAP,
    SLIDING,
    FINISHED,
    RELEASED
}

public class SlideEvent implements GameEvent {
    public static final Duration TOO_EARLY = Duration.ofMillis(750); // 0 scores
    public static final Duration REGULAR = Duration.ofMillis(500); // 100 scores
    public static final Duration PERFECT = Duration.ofMillis(250); // 300 scores

    public static final Duration BEFORE_SLIDING = Duration.ofMillis(1500);

    public SlideEvent(double x1, double y1, double x2, double y2, int checkPointsNumber, Duration slideDuration) {
        this.startX = x1;
        this.startY = y1;
        this.finishX = x2;
        this.finishY = y2;
        this.slideDuration = slideDuration;
        this.checkPointsNumber = checkPointsNumber;
        eventStatus = SlideEventStatus.NOT_STARTED;
        scores = 0;

    }

    @Override
    public void start(Clock clock, Set<GamePresenter> presenters) {
        // set clocks for this event
        this.clock = clock;
        beginningTime = clock.instant();
        presentersSet = presenters;

        eventStatus = SlideEventStatus.AWAITING_TAP;

        Timeline fiveSecondsWonder = new Timeline(new KeyFrame(javafx.util.Duration.millis(BEFORE_SLIDING.toMillis()),
                new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                startSliding();
            }
        }));
        fiveSecondsWonder.play();

        /*
        new Timer().schedule(
                new TimerTask() {

                    @Override
                    public void run() {
                        startSliding();
                    }
                }, 0, BEFORE_SLIDING.toMillis());
                */

        /*
        // set checks for mouse position in check point time and end of event
        executor = Executors.newScheduledThreadPool(4);

        // start sliding after tap event passing
        executor.schedule(this::startSliding, BEFORE_SLIDING.toMillis(), TimeUnit.MILLISECONDS);

        long checkInterval = slideDuration.toMillis() / (checkPointsNumber + 1);
        for (int i = 1; i <= checkPointsNumber; ++i) {
            executor.schedule(this::checkPoint, BEFORE_SLIDING.toMillis() + checkInterval * i, TimeUnit.MILLISECONDS);
        }

        executor.schedule(this::lastPoint, BEFORE_SLIDING.toMillis() + slideDuration.toMillis(), TimeUnit.MILLISECONDS);
        */
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
                scores += 100;
                startSliding();
            }
        }
    }


    public void release() {
//        eventStatus = SlideEventStatus.RELEASED;
    }

    public void setInMovingCircle(boolean b) {
        inMovingCircle = b;
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
        if(SlideEventStatus.AWAITING_TAP == eventStatus) {
            eventStatus = SlideEventStatus.SLIDING;
            for (GamePresenter gp : presentersSet) {
                gp.startSliding(this);
            }
            inMovingCircle = true;
        }
    }

    private void checkPoint() {
        if(inMovingCircle && SlideEventStatus.SLIDING == eventStatus) {
            scores += 25;
            for (GamePresenter gp : presentersSet) {
                gp.pulse(this);
            }
        }
    }

    private void lastPoint() {
        if(inMovingCircle && SlideEventStatus.SLIDING == eventStatus) {
            scores += 100;
            for (GamePresenter gp : presentersSet) {
                gp.pulse(this);
            }
        }
        eventStatus = SlideEventStatus.FINISHED;
    }

    private final double startX;
    private final double startY;
    private final double finishX;
    private final double finishY;

    private final Duration slideDuration;
    private final int checkPointsNumber;
    private SlideEventStatus eventStatus;
    private boolean inMovingCircle;
    private int scores;

    private ScheduledExecutorService executor;
    private Set<GamePresenter> presentersSet;

    private Clock clock;
    private Instant beginningTime;

}

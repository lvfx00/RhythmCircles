package ru.nsu.fit.semenov.rhythmcircles.events;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.LinkedList;
import java.util.concurrent.*;

import static ru.nsu.fit.semenov.rhythmcircles.events.EventType.SLIDE;

enum SlideEventStatus {
    NOT_STARTED,
    AWAITING_TAP,
    SLIDING,
    FINISHED
}

public class SlideEvent implements GameEvent {
    public static final Duration TOO_EARLY = Duration.ofMillis(750); // 0 scores
    public static final Duration REGULAR = Duration.ofMillis(500); // 100 scores
    public static final Duration PERFECT = Duration.ofMillis(250); // 300 scores
    public static final Duration TOO_LATE = Duration.ofMillis(500); // 50 scores

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
    public void start(Clock clock) {
        // set clocks for this event
        this.clock = clock;
        beginningTime = clock.instant();
        eventStatus = SlideEventStatus.AWAITING_TAP;

        // set checks for mouse position in check point time and end of event
        executor = Executors.newSingleThreadScheduledExecutor();

        long checkInterval = slideDuration.toMillis() / (checkPointsNumber + 1);

        for (int i = 1; i <= checkPointsNumber; ++i) {
            Future<Boolean> resultFuture =
                    executor.schedule(() -> inMovingCircle, checkInterval * i, TimeUnit.MILLISECONDS);
            checkPointsResultList.add(resultFuture);
        }

        // for second big circle
        lastCircleResult = executor.schedule(() -> inMovingCircle, slideDuration.toMillis(), TimeUnit.MILLISECONDS);
    }

    public void tap() {
        if (eventStatus == SlideEventStatus.AWAITING_TAP) {

            if (Duration.between(beginningTime, clock.instant()).compareTo(TOO_EARLY) < 0) {
                scores += 0;

            } else if (Duration.between(beginningTime, clock.instant()).
                    compareTo(TOO_EARLY.plus(REGULAR)) < 0) {
                scores += 100;

            } else if (Duration.between(beginningTime, clock.instant()).
                    compareTo(TOO_EARLY.plus(REGULAR).plus(PERFECT)) < 0) {
                scores += 300;

            } else if (Duration.between(beginningTime, clock.instant()).
                    compareTo(TOO_EARLY.plus(REGULAR).plus(PERFECT).plus(TOO_LATE)) < 0) {
                scores += 50;
            }

            eventStatus = SlideEventStatus.SLIDING;
            inMovingCircle = true;
        }
    }

    public void release() {
        eventStatus = SlideEventStatus.FINISHED;
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
                for (Future<Boolean> futureResult : checkPointsResultList) {
                    if (futureResult.isDone()) {
                        try {
                            boolean result = futureResult.get();
                            if (result) {
                                scores += 50;
                            }
                        } catch (InterruptedException | ExecutionException e) {
                            e.printStackTrace();
                        }
                    } else {
                        futureResult.cancel(true);
                    }
                }

                if (lastCircleResult.isDone()) {
                    try {
                        boolean result = lastCircleResult.get();
                        if (result) {
                            scores += 150;
                        }
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }
                } else {
                    lastCircleResult.cancel(true);
                }

                executor.shutdown();
            }
            return scores;
        }
        return 0;
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

    ScheduledExecutorService executor;
    private LinkedList<Future<Boolean>> checkPointsResultList = new LinkedList<>();
    private Future<Boolean> lastCircleResult;

    private Clock clock;
    private Instant beginningTime;

}

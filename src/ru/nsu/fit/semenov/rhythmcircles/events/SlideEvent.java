package ru.nsu.fit.semenov.rhythmcircles.events;

import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import ru.nsu.fit.semenov.rhythmcircles.GameModel;
import ru.nsu.fit.semenov.rhythmcircles.GamePresenter;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.*;

import static ru.nsu.fit.semenov.rhythmcircles.MyGameModel.CIRCLE_RADIUS;
import static ru.nsu.fit.semenov.rhythmcircles.events.EventType.SLIDE;

public class SlideEvent implements GameEvent {
    private enum SlideEventStatus {
        NOT_STARTED,
        AWAITING_TAP,
        SLIDING,
        FINISHED,
    }

    private static final Duration TAP_TOO_EARLY = Duration.ofMillis(750);
    private static final Duration TAP_REGULAR = Duration.ofMillis(500);
    private static final Duration TAP_PERFECT = Duration.ofMillis(250);
    private static final Duration TAP_TOO_LATE = Duration.ofMillis(500);

    public static final Duration BEFORE_SLIDING = Duration.ofMillis(1375);

    public SlideEvent(double x1, double y1, double x2, double y2, Duration slideDuration) {
        this.startX = x1;
        this.startY = y1;
        this.finishX = x2;
        this.finishY = y2;
        this.slideDuration = slideDuration;
        eventStatus = SlideEventStatus.NOT_STARTED;
        scores = 0;
        // set event bounds
        eventBounds = calcEventBounds(x1, y1, x2, y2);
    }

    @Override
    public void start(Clock clock, GameModel gameModel) {
        // set clocks for this event
        this.clock = clock;
        beginningTime = clock.instant();
        eventStatus = SlideEventStatus.AWAITING_TAP;

        executor = Executors.newSingleThreadScheduledExecutor();
        executor.schedule(() -> {
                    if (SlideEventStatus.AWAITING_TAP == eventStatus) {
                        eventStatus = SlideEventStatus.SLIDING;
                        gameModel.submitEventTask((GamePresenter presenter) -> presenter.startSliding(this));
                    }
                }, BEFORE_SLIDING.toMillis(), TimeUnit.MILLISECONDS);

        executor.schedule(() -> {
                    if(mouseInCircle) {
                        scores += 150;
                        gameModel.submitEventTask((GamePresenter presenter) -> presenter.pulse(this));
                    }
                    eventStatus = SlideEventStatus.FINISHED;
                }, BEFORE_SLIDING.plus(slideDuration).toMillis(), TimeUnit.MILLISECONDS);
    }

    public void tap() {
        if (eventStatus == SlideEventStatus.AWAITING_TAP) {

            if (Duration.between(beginningTime, clock.instant()).compareTo(TAP_TOO_EARLY) < 0) {
                scores += 0;

            } else if (Duration.between(beginningTime, clock.instant()).
                    compareTo(TAP_TOO_EARLY.plus(TAP_REGULAR)) < 0) {
                scores += 100;
                mouseInCircle = true;
                startSliding();

            } else if (Duration.between(beginningTime, clock.instant()).
                    compareTo(TAP_TOO_EARLY.plus(TAP_REGULAR).plus(TAP_PERFECT)) < 0) {
                scores += 200;
                mouseInCircle = true;
                startSliding();

            } else if (Duration.between(beginningTime, clock.instant()).
                    compareTo(TAP_TOO_EARLY.plus(TAP_REGULAR).plus(TAP_PERFECT).plus(TAP_TOO_LATE)) < 0) {
                scores += 50;
            }
        }
    }

    public void release() {
        eventStatus = SlideEventStatus.FINISHED;
    }

    public void setMouseInCircle(boolean b) {
        mouseInCircle = b;
    }

    public static EventBounds calcEventBounds(double x1, double y1, double x2, double y2) {
        Circle startCircle = new Circle(CIRCLE_RADIUS);
        startCircle.setCenterX(x1);
        startCircle.setCenterY(y1);

        Circle finishCircle = new Circle(CIRCLE_RADIUS);
        finishCircle.setCenterX(x2);
        finishCircle.setCenterY(y2);

        double rectWidth = Math.sqrt(Math.pow(x2 - x1, 2.0) + Math.pow(y2 - y1, 2.0));
        double rectHeight = 2 * CIRCLE_RADIUS;

        Rectangle slidePath = new Rectangle(rectWidth, rectHeight);

        // set center of rectangle to path center
        double rectCenterX = (x1 + x2) / 2;
        double rectCenterY = (y1 + y2) / 2;
        slidePath.setX(rectCenterX - rectWidth / 2);
        slidePath.setY(rectCenterY - rectHeight / 2);

        // rotate rectangle
        double rotationDegree = Math.toDegrees(Math.atan((y2 - y1) / (x2 - x1)));
        slidePath.setRotate(rotationDegree);

        Group boundsGroup = new Group(startCircle, finishCircle, slidePath);
        Bounds bounds = boundsGroup.getBoundsInLocal();

        return new EventBounds(bounds);
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
    public EventBounds getEventBounds() {
        return eventBounds;
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
    }

    private final double startX;
    private final double startY;
    private final double finishX;
    private final double finishY;

    private final Duration slideDuration;

    private final EventBounds eventBounds;

    private SlideEventStatus eventStatus;
    private boolean mouseInCircle;
    private int scores;

    private ScheduledExecutorService executor;

    private Clock clock;
    private Instant beginningTime;

}

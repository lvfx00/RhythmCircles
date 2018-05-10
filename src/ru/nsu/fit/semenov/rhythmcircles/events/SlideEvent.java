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
import static ru.nsu.fit.semenov.rhythmcircles.events.TapEvent.*;

public class SlideEvent implements GameEvent {
    private enum SlideEventStatus {
        NOT_STARTED,
        AWAITING_TAP,
        SLIDING,
        FINISHED,
    }

    public SlideEvent(double x1, double y1, double x2, double y2, Duration forward, Duration backward) {
        this.startX = x1;
        this.startY = y1;
        this.finishX = x2;
        this.finishY = y2;
        this.forward = forward;
        if (null == backward) {
            hasBackward = false;
        } else {
            hasBackward = true;
            this.backward = backward;
        }
        eventStatus = SlideEventStatus.NOT_STARTED;
        scores = 0;
        eventBounds = calcEventBounds(x1, y1, x2, y2);
    }


    @Override
    public void start(Clock clock, GameModel gameModel) {
        this.clock = clock;
        beginningTime = clock.instant();
        eventStatus = SlideEventStatus.AWAITING_TAP;
        this.gameModel = gameModel;

        executor = Executors.newSingleThreadScheduledExecutor();
        executor.schedule(this::startSliding, BEFORE_TAP.toMillis(), TimeUnit.MILLISECONDS);

        if (hasBackward) {
            executor.schedule(() -> {
                if (SlideEventStatus.SLIDING == eventStatus && mouseInCircle) {
                    scores += 100;
                    gameModel.submitEventTask((GamePresenter presenter) -> presenter.pulse(finishX, finishY));
                }
            }, BEFORE_TAP.plus(forward).toMillis(), TimeUnit.MILLISECONDS);
            executor.schedule(() -> {
                if (SlideEventStatus.SLIDING == eventStatus && mouseInCircle) {
                    scores += 100;
                    gameModel.submitEventTask((GamePresenter presenter) -> presenter.pulse(startX, startY));
                }
                eventStatus = SlideEventStatus.FINISHED;
            }, BEFORE_TAP.plus(forward).plus(backward).toMillis(), TimeUnit.MILLISECONDS);

        } else {
            executor.schedule(() -> {
                if (SlideEventStatus.SLIDING == eventStatus && mouseInCircle) {
                    scores += 150;
                    gameModel.submitEventTask((GamePresenter presenter) -> presenter.pulse(finishX, finishY));
                }
                eventStatus = SlideEventStatus.FINISHED;
            }, BEFORE_TAP.plus(forward).toMillis(), TimeUnit.MILLISECONDS);
        }
    }


    public void tap() {
        if (eventStatus == SlideEventStatus.AWAITING_TAP) {

            if (Duration.between(beginningTime, clock.instant()).compareTo(TAP_TOO_EARLY) < 0) {
                scores += 0;

            } else if (Duration.between(beginningTime, clock.instant()).
                    compareTo(TAP_TOO_EARLY.plus(TAP_REGULAR)) < 0) {
                scores += 100;

            } else if (Duration.between(beginningTime, clock.instant()).
                    compareTo(TAP_TOO_EARLY.plus(TAP_REGULAR).plus(TAP_PERFECT)) < 0) {
                scores += 150;
            }

            mouseInCircle = true;
            startSliding();

        } else if (eventStatus == SlideEventStatus.SLIDING &&
                Duration.between(beginningTime, clock.instant()).
                        compareTo(TAP_TOO_EARLY.plus(TAP_REGULAR).plus(TAP_PERFECT).plus(TAP_TOO_LATE)) < 0) {

            scores += 50;
        }
    }

    private void startSliding() {
        if (SlideEventStatus.AWAITING_TAP == eventStatus) {
            eventStatus = SlideEventStatus.SLIDING;
            gameModel.submitEventTask((GamePresenter presenter) -> presenter.startSliding(this));
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

    public Duration getSlideForwardDuration() {
        return forward;
    }

    public Duration getSlideBackwardDuration() {
        return backward;
    }

    public boolean isHasBackward() {
        return hasBackward;
    }

    private final double startX;
    private final double startY;
    private final double finishX;
    private final double finishY;

    private Duration forward;
    private Duration backward;
    private GameModel gameModel;
    private final boolean hasBackward;

    private final EventBounds eventBounds;

    private SlideEventStatus eventStatus;
    private boolean mouseInCircle;
    private int scores;

    private ScheduledExecutorService executor;

    private Clock clock;
    private Instant beginningTime;
}

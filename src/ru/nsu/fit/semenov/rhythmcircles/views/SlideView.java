package ru.nsu.fit.semenov.rhythmcircles.views;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Text;
import javafx.util.Duration;
import ru.nsu.fit.semenov.rhythmcircles.animations.Animation;
import ru.nsu.fit.semenov.rhythmcircles.animations.AnimationCarrier;

import java.util.ArrayList;

import static ru.nsu.fit.semenov.rhythmcircles.views.ViewParams.RADIUS;

public class SlideView extends Group implements AnimationCarrier {

    public SlideView(double x1, double y1, double x2, double y2) {
        this.x1 = x1;
        this.x2 = x2;
        this.y1 = y1;
        this.y2 = y2;

        Circle startCircle = new Circle(RADIUS, Color.web("white", 0.15));
        startCircle.setStrokeType(StrokeType.OUTSIDE);
        startCircle.setStroke(Color.web("white", 1));
        startCircle.setStrokeWidth(5);
        startCircle.setCenterX(x1);
        startCircle.setCenterY(y1);

        Circle finishCircle = new Circle(RADIUS, Color.web("white", 0.15));
        finishCircle.setStrokeType(StrokeType.OUTSIDE);
        finishCircle.setStroke(Color.web("white", 1));
        finishCircle.setStrokeWidth(5);
        finishCircle.setCenterX(x2);
        finishCircle.setCenterY(y2);

        double rotationDegree = Math.atan((y2 - y1) / (x2 - x1));
        double dx = Math.sin(rotationDegree) * (RADIUS + 2.5);
        double dy = Math.cos(rotationDegree) * (RADIUS + 2.5);

        Line firstLine = new Line(x1 + dx, y1 - dy, x2 + dx, y2 - dy);
        firstLine.setFill(Color.web("white", 0.05));
        firstLine.setStrokeType(StrokeType.OUTSIDE);
        firstLine.setStroke(Color.web("white", 1));
        firstLine.setStrokeWidth(2.5);

        Line secondLine = new Line(x1 - dx, y1 + dy, x2 - dx, y2 + dy);
        secondLine.setStrokeType(StrokeType.OUTSIDE);
        secondLine.setStroke(Color.web("white", 1));
        secondLine.setStrokeWidth(2.5);

        innerScopeCircle = new Circle(RADIUS, Color.web("white", 0));
        innerScopeCircle.setStrokeType(StrokeType.OUTSIDE);
        innerScopeCircle.setStroke(Color.web("white", 1));
        innerScopeCircle.setStrokeWidth(5);
        innerScopeCircle.setCenterX(x1);
        innerScopeCircle.setCenterY(y1);
        innerScopeCircle.setMouseTransparent(true);

        outerScopeCircle = new Circle(RADIUS + 30, Color.web("white", 0));
        outerScopeCircle.setStrokeType(StrokeType.OUTSIDE);
        outerScopeCircle.setStroke(Color.web("white", 1));
        outerScopeCircle.setStrokeWidth(5);
        outerScopeCircle.setCenterX(x1);
        outerScopeCircle.setCenterY(y1);
        outerScopeCircle.setMouseTransparent(true);

        // add moving circle
        movingCircle = new Circle(RADIUS, Color.web("white", 0));
        movingCircle.setStrokeType(StrokeType.OUTSIDE);
        movingCircle.setStroke(Color.web("white", 1));
        movingCircle.setStrokeWidth(5);
        movingCircle.setCenterX(x1);
        movingCircle.setCenterY(y1);

        super.getChildren().addAll(startCircle, finishCircle, firstLine, secondLine, movingCircle);
    }

    public void startSlidingAnimation(java.time.Duration forward) {
        super.getChildren().addAll(innerScopeCircle);
        super.getChildren().addAll(outerScopeCircle);

        Timeline timeline = new Timeline();
        timeline.getKeyFrames().addAll(
                new KeyFrame(Duration.millis(forward.toMillis()),
                        new KeyValue(outerScopeCircle.centerXProperty(), x2),
                        new KeyValue(outerScopeCircle.centerYProperty(), y2),
                        new KeyValue(innerScopeCircle.centerXProperty(), x2),
                        new KeyValue(innerScopeCircle.centerYProperty(), y2)));

        timeline.setOnFinished(event -> {
            Timeline stopTimeline = new Timeline();
            stopTimeline.getKeyFrames().addAll(
                    new KeyFrame(Duration.millis(300),
                            new KeyValue(outerScopeCircle.opacityProperty(), 0),
                            new KeyValue(innerScopeCircle.opacityProperty(), 0)));
            stopTimeline.setOnFinished(event2 -> {
                getChildren().remove(innerScopeCircle);
                getChildren().remove(outerScopeCircle);
                getChildren().remove(movingCircle);
            });
            stopTimeline.play();
        });
        timeline.play();
    }

    public void startSlidingAnimation(java.time.Duration forward, java.time.Duration backward) {
        super.getChildren().addAll(innerScopeCircle);
        super.getChildren().addAll(outerScopeCircle);

        Timeline timeline = new Timeline();
        timeline.getKeyFrames().addAll(
                new KeyFrame(Duration.millis(forward.toMillis()),
                        new KeyValue(outerScopeCircle.centerXProperty(), x2),
                        new KeyValue(outerScopeCircle.centerYProperty(), y2),
                        new KeyValue(innerScopeCircle.centerXProperty(), x2),
                        new KeyValue(innerScopeCircle.centerYProperty(), y2)));
        timeline.setOnFinished(event -> {
            Timeline timeline2 = new Timeline();
            timeline2.getKeyFrames().addAll(
                    new KeyFrame(Duration.millis(backward.toMillis()),
                            new KeyValue(outerScopeCircle.centerXProperty(), x1),
                            new KeyValue(outerScopeCircle.centerYProperty(), y1),
                            new KeyValue(innerScopeCircle.centerXProperty(), x1),
                            new KeyValue(innerScopeCircle.centerYProperty(), y1)));
            timeline2.setOnFinished(event2 -> {
                Timeline stopTimeline = new Timeline();
                stopTimeline.getKeyFrames().addAll(
                        new KeyFrame(Duration.millis(300),
                                new KeyValue(outerScopeCircle.opacityProperty(), 0),
                                new KeyValue(innerScopeCircle.opacityProperty(), 0)));
                stopTimeline.setOnFinished(event3 -> {
                    getChildren().remove(innerScopeCircle);
                    getChildren().remove(outerScopeCircle);
                    getChildren().remove(movingCircle);
                });
                stopTimeline.play();
            });
            timeline2.play();
        });
        timeline.play();
    }

    public void addArrow() {
        Text numberText = new Text("→");
        numberText.setId("numbertext");
        numberText.setMouseTransparent(true);
        numberText.setX(x2 - 20);
        numberText.setY(y2 + 20);

        double rotationDegree = Math.atan((y2 - y1) / (x2 - x1));
        numberText.setRotate(Math.toDegrees(rotationDegree));

        super.getChildren().add(numberText);
    }

    public Circle getMovingCircle() {
        return movingCircle;
    }

    public double getScopeCircleX() {
        return innerScopeCircle.getCenterX();
    }

    public double getScopeCircleY() {
        return innerScopeCircle.getCenterY();
    }

    @Override
    public void addAnimation(Animation animation) {
        animationList.add(animation);
        animation.run();
    }

    @Override
    public void removeAllAnimations() {
        for (Animation an : animationList) {
            if (!an.finished()) {
                an.stop();
            }
        }
        animationList.clear();
    }

    private ArrayList<Animation> animationList = new ArrayList<>();

    private final double x1, x2, y1, y2;
    private final Circle innerScopeCircle;
    private final Circle outerScopeCircle;
    private final Circle movingCircle;
}

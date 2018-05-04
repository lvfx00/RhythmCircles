package ru.nsu.fit.semenov.rhythmcircles.animations;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.Group;
import javafx.scene.effect.BoxBlur;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.StrokeType;
import javafx.util.Duration;

import static ru.nsu.fit.semenov.rhythmcircles.views.ViewParams.RADIUS;

public class ScopeCircle implements Animation {
    public ScopeCircle(double x1, double y1, double x2, double y2, Duration duration, Group group) {
        finished = false;
        this.group = group;

        Circle innerCircle = new Circle(RADIUS, Color.web("white", 0));
        innerCircle.setStrokeType(StrokeType.OUTSIDE);
        innerCircle.setStroke(Color.web("white", 0.7));
        innerCircle.setStrokeWidth(4);
        innerCircle.setCenterX(x1);
        innerCircle.setCenterY(y1);
        innerCircle.setEffect(new BoxBlur(10, 10, 3));
        innerCircle.setMouseTransparent(true);

        Circle outerCircle = new Circle(RADIUS + 30, Color.web("white", 0));
        outerCircle.setStrokeType(StrokeType.OUTSIDE);
        outerCircle.setStroke(Color.web("white", 0.7));
        outerCircle.setStrokeWidth(4);
        outerCircle.setCenterX(x1);
        outerCircle.setCenterY(y1);
        outerCircle.setEffect(new BoxBlur(10, 10, 3));
        outerCircle.setMouseTransparent(true);

        scopeGroup = new Group(innerCircle, outerCircle);

        group.getChildren().addAll(scopeGroup);

        timeline = new Timeline();
        timeline.getKeyFrames().addAll(
                new KeyFrame(Duration.millis(duration.toMillis()),
                        new KeyValue(outerCircle.centerXProperty(), x2),
                        new KeyValue(outerCircle.centerYProperty(), y2),
                        new KeyValue(innerCircle.centerXProperty(), x2),
                        new KeyValue(innerCircle.centerYProperty(), y2)));
        timeline.setOnFinished(event -> stop());
    }


    @Override
    public void run() {
        timeline.play();
    }

    @Override
    public void stop() {
        timeline.stop();

        Timeline stopTimeline = new Timeline();
        stopTimeline.getKeyFrames().addAll(
                new KeyFrame(Duration.millis(300),
                        new KeyValue(scopeGroup.opacityProperty(), 0)));
        timeline.setOnFinished(event -> group.getChildren().remove(scopeGroup));
        stopTimeline.play();
        finished = true;
    }

    @Override
    public boolean finished() {
        return finished;
    }

    private boolean finished;
    private Timeline timeline;
    private Group group;

    private Group scopeGroup;
}

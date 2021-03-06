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

public class CompressiveRing implements Animation {

    public CompressiveRing(double x, double y, Duration duration, Group group) {
        finished = false;
        this.group = group;

        ring = new Circle(200, Color.web("white", 0));
        ring.setCenterX(x);
        ring.setCenterY(y);

        ring.setStrokeType(StrokeType.OUTSIDE);
        ring.setStroke(Color.web("white", 1));
        ring.setStrokeWidth(3);
        ring.setEffect(new BoxBlur(5, 5, 5));

        ring.setMouseTransparent(true);

        group.getChildren().add(ring);

        timeline = new Timeline();
        timeline.getKeyFrames().addAll(
                new KeyFrame(duration,
                        new KeyValue(ring.radiusProperty(), RADIUS)));
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
                        new KeyValue(ring.opacityProperty(), 0)));
        timeline.setOnFinished(event -> group.getChildren().remove(ring));
        stopTimeline.play();
        finished = true;
    }

    @Override
    public boolean finished() {
        return finished;
    }

    private boolean finished;
    private Timeline timeline;
    private Circle ring;
    private Group group;
}

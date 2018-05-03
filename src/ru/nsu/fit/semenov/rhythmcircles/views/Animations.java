package ru.nsu.fit.semenov.rhythmcircles.views;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.Group;
import javafx.scene.effect.BoxBlur;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Text;
import javafx.util.Duration;

import static ru.nsu.fit.semenov.rhythmcircles.views.ViewParams.RADIUS;

public class Animations {

    public static void ringAnimation(double x, double y, Duration duration, Group group) {
        Circle ring = new Circle(150, Color.web("white", 0));
        ring.setCenterX(x);
        ring.setCenterY(y);

        ring.setStrokeType(StrokeType.OUTSIDE);
        ring.setStroke(Color.web("white", 0.7));
        ring.setStrokeWidth(2);
        ring.setEffect(new BoxBlur(10, 10, 3));

        ring.setMouseTransparent(true);

        group.getChildren().add(ring);

        Timeline ringTimeline = new Timeline();
        ringTimeline.getKeyFrames().addAll(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(ring.radiusProperty(), 150)),
                new KeyFrame(duration,
                        new KeyValue(ring.radiusProperty(), RADIUS)));

        ringTimeline.setOnFinished(event -> group.getChildren().remove(ring));
        ringTimeline.play();
    }

    public static void showScores(double x, double y, int scores, Group group) {
        Text scoreText = new Text();
        scoreText.setId("scoretext");
        scoreText.setText(String.valueOf(scores));

        group.getChildren().add(scoreText);

        Timeline timeline = new Timeline();
        timeline.getKeyFrames().addAll(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(scoreText.opacityProperty(), 1),
                        new KeyValue(scoreText.xProperty(), x),
                        new KeyValue(scoreText.yProperty(), y)),
                new KeyFrame(new Duration(1000),
                        new KeyValue(scoreText.opacityProperty(), 0),
                        new KeyValue(scoreText.xProperty(), x + 15),
                        new KeyValue(scoreText.yProperty(), y + 30)));
        timeline.setOnFinished(event -> group.getChildren().remove(scoreText));
        timeline.play();
    }
}

package ru.nsu.fit.semenov.rhythmcircles.animations;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.Group;
import javafx.scene.effect.BoxBlur;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class OrderingNumber implements Animation {

    public OrderingNumber(double x, double y, int num, Group group) {
        numberText = new Text(String.valueOf(num));
        numberText.setId("numbertext");
        numberText.setMouseTransparent(true);
        numberText.setOpacity(0);
        numberText.setX(x - 20);
        numberText.setY(y + 20);

        finished = false;
        this.group = group;

        group.getChildren().add(numberText);

        Timeline timeline = new Timeline();
        timeline.getKeyFrames().addAll(
                new KeyFrame(Duration.millis(300),
                        new KeyValue(numberText.opacityProperty(), 1)));
        timeline.play();
    }

    @Override
    public void run() {
    }

    @Override
    public void stop() {
        Timeline stopTimeline = new Timeline();
        stopTimeline.getKeyFrames().addAll(
                new KeyFrame(Duration.millis(300),
                        new KeyValue(numberText.opacityProperty(), 0)));
        stopTimeline.setOnFinished(event -> group.getChildren().remove(numberText));
        stopTimeline.play();
        finished = true;
    }

    @Override
    public boolean finished() {
        return finished;
    }

    private boolean finished;
    private Text numberText;
    private Group group;
}


package ru.nsu.fit.semenov.rhythmcircles.views.animations;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.Group;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class ShowScores implements Animation {
    public ShowScores(double x, double y, int scores, Group group) {
        finished = false;
        this.group = group;

        scoreText = new Text();
        scoreText.setId("scoretext");
        scoreText.setText(String.valueOf(scores));

        group.getChildren().add(scoreText);

        timeline = new Timeline();
        timeline.getKeyFrames().addAll(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(scoreText.opacityProperty(), 1),
                        new KeyValue(scoreText.xProperty(), x),
                        new KeyValue(scoreText.yProperty(), y)),
                new KeyFrame(new Duration(1000),
                        new KeyValue(scoreText.opacityProperty(), 0),
                        new KeyValue(scoreText.xProperty(), x + 15),
                        new KeyValue(scoreText.yProperty(), y + 30)));
        timeline.setOnFinished(event -> stop());
        finished = true;
    }


    @Override
    public void run() {
        timeline.play();
    }

    @Override
    public void stop() {
        timeline.stop();
        group.getChildren().remove(scoreText);
    }

    @Override
    public boolean finished() {
        return finished;
    }

    private boolean finished;
    private Timeline timeline;
    private Text scoreText;
    private Group group;
}

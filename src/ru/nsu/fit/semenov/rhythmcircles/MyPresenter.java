package ru.nsu.fit.semenov.rhythmcircles;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.util.Duration;
import ru.nsu.fit.semenov.rhythmcircles.events.GameEvent;
import ru.nsu.fit.semenov.rhythmcircles.events.TapEvent;

import java.util.HashMap;

public class MyPresenter implements GamePresenter {
    public MyPresenter(GameModel gm, Group root, Group cg) {
        gameModel = gm;
        circlesGroup = cg;
        rootGroup = root;
    }

    @Override
    public void addEventView(GameEvent ge) {
        switch (ge.getEventType()) {
            case TAP:
                CircleView circle = new CircleView();
                circle.relocate(((TapEvent) ge).getX(), ((TapEvent) ge).getY());

                // TODO add showing animation
                circlesGroup.getChildren().add(circle);

                circle.addEventFilter(MouseEvent.MOUSE_PRESSED,
                        mouseEvent -> viewToEvnt.get(circle).tap());


                evntToView.put((TapEvent) ge, circle);
                viewToEvnt.put(circle, (TapEvent) ge);
                break;
        }
    }

    @Override
    public void removeEventView(GameEvent gameEvent) {
        switch (gameEvent.getEventType()) {
            case TAP:
                CircleView circle = evntToView.get(gameEvent);

                // TODO add disappearing animation
                circlesGroup.getChildren().remove(circle);

                evntToView.remove(gameEvent);
                viewToEvnt.remove(circle);
                break;
        }
    }

    @Override
    public void showScores(GameEvent gameEvent, int scores) {
        Text scoreText = new Text();
        rootGroup.getChildren().add(scoreText);
        scoreText.setId("scoretext");
        scoreText.setText(String.valueOf(scores));

        double x = 0;
        double y = 0;

        // find out where to show scores
        switch (gameEvent.getEventType()) {
            case TAP:
                CircleView circle = evntToView.get((TapEvent) gameEvent);
                x = circle.getBoundsInParent().getMaxX() - circle.getRadius() * 0.5;
                y = circle.getBoundsInParent().getMaxY() - circle.getRadius() * 0.5;
                break;
        }

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
        timeline.setOnFinished(event -> rootGroup.getChildren().remove(scoreText));
        timeline.play();
    }


    private final Group rootGroup;
    private final Group circlesGroup;
    private final GameModel gameModel;
    private HashMap<TapEvent, CircleView> evntToView = new HashMap<>();
    private HashMap<CircleView, TapEvent> viewToEvnt = new HashMap<>();


}

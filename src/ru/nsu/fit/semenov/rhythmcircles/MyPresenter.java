package ru.nsu.fit.semenov.rhythmcircles;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.util.Duration;
import ru.nsu.fit.semenov.rhythmcircles.animations.OrderingNumber;
import ru.nsu.fit.semenov.rhythmcircles.events.SlideEvent;
import ru.nsu.fit.semenov.rhythmcircles.events.TapEvent;
import ru.nsu.fit.semenov.rhythmcircles.views.SlideView;
import ru.nsu.fit.semenov.rhythmcircles.views.TapView;
import ru.nsu.fit.semenov.rhythmcircles.animations.CompressiveRing;
import ru.nsu.fit.semenov.rhythmcircles.animations.Pulse;

import java.util.HashMap;

import static ru.nsu.fit.semenov.rhythmcircles.views.ViewParams.RADIUS;

public class MyPresenter implements GamePresenter {
    public MyPresenter(Group main, Group cg) {
        mainGroup = main;
        rainbowGroup = cg;
    }


    @Override
    public void addTapEventView(TapEvent tapEvent, int num) {
        TapView tapView = new TapView(tapEvent.getX(), tapEvent.getY());
        tapView.addAnimation(new OrderingNumber(tapEvent.getX(), tapEvent.getY(), num, rainbowGroup));
        tapView.setOpacity(0);

        tapEvntToView.put(tapEvent, tapView);

        rainbowGroup.getChildren().add(tapView);

        // showing animation
        Timeline timeline = new Timeline();
        timeline.getKeyFrames().addAll(
                new KeyFrame(Duration.millis(300),
                        new KeyValue(tapView.opacityProperty(), 1)));
        timeline.play();

    }


    @Override
    public void startTapEvent(TapEvent tapEvent) {
        if (tapEvntToView.containsKey(tapEvent)) {
            TapView tapView = tapEvntToView.get(tapEvent);

            tapView.addAnimation(new CompressiveRing(tapEvent.getX(), tapEvent.getY(), Duration.millis(
                    TapEvent.TOO_EARLY.plus(TapEvent.REGULAR).plus(TapEvent.PERFECT).toMillis()), rainbowGroup));

            // event handlers
            tapView.getCircle().setOnMousePressed(t -> {
                tapEvent.tap();
                tapView.getCircle().setFill(Color.web("white", 0.5));
            });
        }
    }


    @Override
    public void removeTapEventView(TapEvent tapEvent) {
        if (tapEvntToView.containsKey(tapEvent)) {
            TapView tapView = tapEvntToView.get(tapEvent);

            tapView.removeAllAnimations();

            // show scores
            showScores(tapEvent.getX(), tapEvent.getY(), tapEvent.getScores());

            // fading animation
            Timeline timeline = new Timeline();
            timeline.getKeyFrames().addAll(
                    new KeyFrame(Duration.millis(300),
                            new KeyValue(tapView.opacityProperty(), 0)));
            timeline.setOnFinished(event -> {
                mainGroup.getChildren().remove(tapView);
                tapEvntToView.remove(tapEvent);
            });
            timeline.play();
        }
    }


    @Override
    public void addSlideEventView(SlideEvent slideEvent, int num) {

        SlideView slideView = new SlideView(slideEvent.getStartX(), slideEvent.getStartY(),
                slideEvent.getFinishX(), slideEvent.getFinishY(), slideEvent.getSlideDuration());
        slideView.addAnimation(new OrderingNumber(slideEvent.getStartX(), slideEvent.getStartY(), num, rainbowGroup));
        slideView.setOpacity(0);

        slideEvntToView.put(slideEvent, slideView);

        rainbowGroup.getChildren().add(slideView);

        // showing animation
        Timeline showingTimeline = new Timeline();
        showingTimeline.getKeyFrames().addAll(
                new KeyFrame(Duration.millis(300),
                        new KeyValue(slideView.opacityProperty(), 1)));
        showingTimeline.play();

        Circle movingCircle = slideView.getMovingCircle();

        movingCircle.setOnMousePressed(t -> {
            orgSceneX = t.getSceneX();
            orgSceneY = t.getSceneY();
            Circle c = (Circle) t.getSource();
            orgTranslateX = c.getTranslateX();
            orgTranslateY = c.getTranslateY();
            slideEvent.tap();
        });

        movingCircle.setOnMouseDragged(t -> {
            double offsetX = t.getSceneX() - orgSceneX;
            double offsetY = t.getSceneY() - orgSceneY;
            double newTranslateX = orgTranslateX + offsetX;
            double newTranslateY = orgTranslateY + offsetY;


            Circle c = (Circle) t.getSource();
            c.setTranslateX(newTranslateX);
            c.setTranslateY(newTranslateY);

            if (Math.sqrt(Math.pow(c.getCenterX() + newTranslateX - slideView.getScopeCircleX(), 2.0) +
                    Math.pow(c.getCenterY() + newTranslateY - slideView.getScopeCircleY(), 2.0)) < RADIUS) {
                slideEvent.setMouseInCircle(true);
                c.setFill(Color.web("white", 0.4));
            } else {
                slideEvent.setMouseInCircle(false);
                c.setFill(Color.web("white", 0));
            }
        });

        movingCircle.setOnMouseReleased(t -> {
            slideEvent.release();
        });
    }


    @Override
    public void startSlideEvent(SlideEvent slideEvent) {
        if (slideEvntToView.containsKey(slideEvent)) {
            SlideView slideView = slideEvntToView.get(slideEvent);

            // add ring animation
            slideView.addAnimation(new CompressiveRing(slideEvent.getStartX(), slideEvent.getStartY(), Duration.millis(
                    TapEvent.TOO_EARLY.plus(TapEvent.REGULAR).plus(TapEvent.PERFECT).toMillis()), rainbowGroup));

        }
    }


    @Override
    public void removeSlideEventView(SlideEvent slideEvent) {
        if (slideEvntToView.containsKey(slideEvent)) {
            SlideView slideView = slideEvntToView.get(slideEvent);

            slideView.removeAllAnimations();

            double x = slideView.getMovingCircle().getCenterX() + slideView.getMovingCircle().getTranslateX();
            double y = slideView.getMovingCircle().getCenterY() + slideView.getMovingCircle().getTranslateY();
            showScores(x, y, slideEvent.getScores());

            // fading animation
            Timeline timeline = new Timeline();
            timeline.getKeyFrames().addAll(
                    new KeyFrame(Duration.millis(300),
                            new KeyValue(slideView.opacityProperty(), 0)));
            timeline.setOnFinished(event -> {
                mainGroup.getChildren().remove(slideView);
                slideEvntToView.remove(slideEvent);
            });
            timeline.play();
        }
    }


    @Override
    public void startSliding(SlideEvent slideEvent) {
        if (slideEvntToView.containsKey(slideEvent)) {
            SlideView slideView = slideEvntToView.get(slideEvent);
            slideView.startSlidingAnimation();
        }
    }

    @Override
    public void pulse(SlideEvent slideEvent) {
        if (slideEvntToView.containsKey(slideEvent)) {
            SlideView slideView = slideEvntToView.get(slideEvent);
            slideView.addAnimation(new Pulse(slideEvent.getFinishX(), slideEvent.getFinishY(), rainbowGroup));
        }
    }

    private void showScores(double x, double y, int scores) {
        Text scoreText = new Text();
        scoreText.setId("scoretext");
        scoreText.setText(String.valueOf(scores));
        scoreText.setX(x);
        scoreText.setY(y);
        mainGroup.getChildren().add(scoreText);

        Timeline timeline = new Timeline();
        timeline.getKeyFrames().addAll(
                new KeyFrame(new Duration(1000),
                        new KeyValue(scoreText.opacityProperty(), 0),
                        new KeyValue(scoreText.translateXProperty(), 20),
                        new KeyValue(scoreText.translateYProperty(), 30)));
        timeline.setOnFinished(t -> mainGroup.getChildren().remove(scoreText));
        timeline.play();
    }

    private final Group mainGroup;
    private final Group rainbowGroup;

    private HashMap<TapEvent, TapView> tapEvntToView = new HashMap<>();
    private HashMap<SlideEvent, SlideView> slideEvntToView = new HashMap<>();

    private double orgSceneX, orgSceneY;
    private double orgTranslateX, orgTranslateY;
}

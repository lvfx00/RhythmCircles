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

import java.util.HashMap;

import static ru.nsu.fit.semenov.rhythmcircles.views.ViewParams.RADIUS;

public class MyPresenter implements GamePresenter {
    public MyPresenter(Group main, Group cg) {
        mainGroup = main;
        rainbowGroup = cg;
    }


    @Override
    public void add(TapEvent event, int num) {
        TapView view = new TapView(event.getX(), event.getY());
        view.addAnimation(new OrderingNumber(event.getX(), event.getY(), num, rainbowGroup));
        view.setOpacity(0);

        tapEvntToView.put(event, view);

        rainbowGroup.getChildren().add(view);

        // showing animation
        Timeline timeline = new Timeline();
        timeline.getKeyFrames().addAll(
                new KeyFrame(Duration.millis(300),
                        new KeyValue(view.opacityProperty(), 1)));
        timeline.play();

    }


    @Override
    public void start(TapEvent event) {
        if (tapEvntToView.containsKey(event)) {
            TapView view = tapEvntToView.get(event);

            view.addAnimation(new CompressiveRing(event.getX(), event.getY(), Duration.millis(
                    TapEvent.TAP_TOO_EARLY.plus(TapEvent.TAP_REGULAR).plus(TapEvent.TAP_PERFECT).toMillis()), rainbowGroup));

            // event handlers
            view.getCircle().setOnMousePressed(t -> {
                event.tap();
                view.getCircle().setFill(Color.web("white", 0.65));
            });
        }
    }


    @Override
    public void remove(TapEvent event) {
        if (tapEvntToView.containsKey(event)) {
            TapView view = tapEvntToView.get(event);

            view.removeAllAnimations();

            // show scores
            showScores(event.getX(), event.getY(), event.getScores());

            // fading animation
            Timeline timeline = new Timeline();
            timeline.getKeyFrames().addAll(
                    new KeyFrame(Duration.millis(300),
                            new KeyValue(view.opacityProperty(), 0)));
            timeline.setOnFinished(e -> {
                mainGroup.getChildren().remove(view);
                tapEvntToView.remove(event);
            });
            timeline.play();
        }
    }


    @Override
    public void add(SlideEvent event, int num) {

        SlideView view = new SlideView(event.getStartX(), event.getStartY(),
                event.getFinishX(), event.getFinishY());
        if(event.isHasBackward()) {
            view.addArrow();
        }
        view.addAnimation(new OrderingNumber(event.getStartX(), event.getStartY(), num, rainbowGroup));
        view.setOpacity(0);

        slideEvntToView.put(event, view);

        rainbowGroup.getChildren().add(view);

        // showing animation
        Timeline showingTimeline = new Timeline();
        showingTimeline.getKeyFrames().addAll(
                new KeyFrame(Duration.millis(300),
                        new KeyValue(view.opacityProperty(), 1)));
        showingTimeline.play();

        Circle movingCircle = view.getMovingCircle();

        movingCircle.setOnMousePressed(t -> {
            orgSceneX = t.getSceneX();
            orgSceneY = t.getSceneY();
            Circle c = (Circle) t.getSource();
            orgTranslateX = c.getTranslateX();
            orgTranslateY = c.getTranslateY();
            event.tap();
        });

        movingCircle.setOnMouseDragged(t -> {
            double offsetX = t.getSceneX() - orgSceneX;
            double offsetY = t.getSceneY() - orgSceneY;
            double newTranslateX = orgTranslateX + offsetX;
            double newTranslateY = orgTranslateY + offsetY;

            Circle c = (Circle) t.getSource();
            c.setTranslateX(newTranslateX);
            c.setTranslateY(newTranslateY);

            if (Math.sqrt(Math.pow(c.getCenterX() + newTranslateX - view.getScopeCircleX(), 2.0) +
                    Math.pow(c.getCenterY() + newTranslateY - view.getScopeCircleY(), 2.0)) < RADIUS) {
                event.setMouseInCircle(true);
                c.setFill(Color.web("white", 0.65));
            } else {
                event.setMouseInCircle(false);
                c.setFill(Color.web("white", 0.15));
            }
        });

        movingCircle.setOnMouseReleased(t -> {
            event.release();
        });
    }


    @Override
    public void start(SlideEvent event) {
        if (slideEvntToView.containsKey(event)) {
            SlideView view = slideEvntToView.get(event);

            // add ring animation
            view.addAnimation(new CompressiveRing(event.getStartX(), event.getStartY(), Duration.millis(
                    TapEvent.BEFORE_TAP.toMillis()), rainbowGroup));
        }
    }


    @Override
    public void remove(SlideEvent event) {
        if (slideEvntToView.containsKey(event)) {
            SlideView view = slideEvntToView.get(event);

            view.removeAllAnimations();

            double x = view.getMovingCircle().getCenterX() + view.getMovingCircle().getTranslateX();
            double y = view.getMovingCircle().getCenterY() + view.getMovingCircle().getTranslateY();
            showScores(x, y, event.getScores());

            // fading animation
            Timeline timeline = new Timeline();
            timeline.getKeyFrames().addAll(
                    new KeyFrame(Duration.millis(300),
                            new KeyValue(view.opacityProperty(), 0)));
            timeline.setOnFinished(e -> {
                mainGroup.getChildren().remove(view);
                slideEvntToView.remove(event);
            });
            timeline.play();
        }
    }


    @Override
    public void startSliding(SlideEvent event) {
        if (slideEvntToView.containsKey(event)) {
            SlideView view = slideEvntToView.get(event);

            if(event.isHasBackward()) {
                view.startSlidingAnimation(event.getSlideForwardDuration(), event.getSlideBackwardDuration());
            }
            else {
                view.startSlidingAnimation(event.getSlideForwardDuration());
            }
        }
    }

    @Override
    public void pulse(double x, double y) {
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

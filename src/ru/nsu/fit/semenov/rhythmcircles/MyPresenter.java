package ru.nsu.fit.semenov.rhythmcircles;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.Group;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;
import ru.nsu.fit.semenov.rhythmcircles.events.SlideEvent;
import ru.nsu.fit.semenov.rhythmcircles.events.TapEvent;
import ru.nsu.fit.semenov.rhythmcircles.views.SlideView;
import ru.nsu.fit.semenov.rhythmcircles.views.TapView;
import ru.nsu.fit.semenov.rhythmcircles.views.animations.CompressiveRing;
import ru.nsu.fit.semenov.rhythmcircles.views.animations.Pulse;
import ru.nsu.fit.semenov.rhythmcircles.views.animations.ScopeCircle;
import ru.nsu.fit.semenov.rhythmcircles.views.animations.ShowScores;

import java.util.HashMap;

public class MyPresenter implements GamePresenter {

    public MyPresenter(Group root, Group cg) {
        circlesGroup = cg;
        rootGroup = root;
    }

    @Override
    public void addTapEventView(TapEvent tapEvent) {
        TapView tapView = new TapView(tapEvent.getX(), tapEvent.getY());
        tapView.setOpacity(0);

        tapEvntToView.put(tapEvent, tapView);

        circlesGroup.getChildren().add(tapView);

        // event handlers
        tapView.getCircle().addEventFilter(MouseEvent.MOUSE_PRESSED, mouseEvent -> tapEvent.tap());

        // showing animation
        Timeline timeline = new Timeline();
        timeline.getKeyFrames().addAll(
                new KeyFrame(Duration.millis(300),
                        new KeyValue(tapView.opacityProperty(), 1)));
        timeline.play();

        // add ring animation
        tapView.addAnimation(new CompressiveRing(tapEvent.getX(), tapEvent.getY(), Duration.millis(
                TapEvent.TOO_EARLY.plus(TapEvent.REGULAR).plus(TapEvent.PERFECT).toMillis()), rootGroup));
    }


    @Override
    public void removeTapEventView(TapEvent tapEvent) {
        TapView tapView = tapEvntToView.get(tapEvent);

        tapView.removeAllAnimations();

        // show scores
        double x = tapView.getCircle().getCenterX();
        double y = tapView.getCircle().getCenterY();
        tapView.addAnimation(new ShowScores(x, y, tapEvent.getScores(), rootGroup));

        // fading animation
        Timeline timeline = new Timeline();
        timeline.getKeyFrames().addAll(
                new KeyFrame(Duration.millis(300),
                        new KeyValue(tapView.opacityProperty(), 0)));
        timeline.setOnFinished(event -> {
            rootGroup.getChildren().remove(tapView);
            tapEvntToView.remove(tapEvent);
        });
        timeline.play();
    }


    @Override
    public void addSlideEventView(SlideEvent slideEvent) {

        SlideView slideView = new SlideView(slideEvent.getStartX(), slideEvent.getStartY(),
                slideEvent.getFinishX(), slideEvent.getFinishY());

        slideView.setOpacity(0);

        slideEvntToView.put(slideEvent, slideView);

        // TODO ???? circles group ????
        circlesGroup.getChildren().add(slideView);

        // event handlers
        slideView.getStartCircle().addEventFilter(MouseEvent.MOUSE_PRESSED, mouseEvent -> slideEvent.tap());
        slideView.getFinishCircle().addEventFilter(MouseEvent.MOUSE_RELEASED, mouseEvent -> {
            slideEvent.release();
            slideEvntToView.get(slideEvent).addAnimation(
                    new Pulse(slideEvent.getFinishX(), slideEvent.getFinishY(), rootGroup));
        });

        // showing animation
        Timeline showingTimeline = new Timeline();
        showingTimeline.getKeyFrames().addAll(
                new KeyFrame(Duration.millis(300),
                        new KeyValue(slideView.opacityProperty(), 1)));
        showingTimeline.play();

        // add ring animation
        slideView.addAnimation(new CompressiveRing(slideEvent.getStartX(), slideEvent.getStartY(), Duration.millis(
                TapEvent.TOO_EARLY.plus(TapEvent.REGULAR).plus(TapEvent.PERFECT).toMillis()), rootGroup));
    }


    @Override
    public void removeSlideEventView(SlideEvent slideEvent) {
        SlideView slideView = slideEvntToView.get(slideEvent);

        slideView.removeAllAnimations();

        // show scores
        double x = slideView.getFinishCircle().getCenterX();
        double y = slideView.getFinishCircle().getCenterY();
        slideView.addAnimation(new ShowScores(x, y, slideEvent.getScores(), rootGroup));

        // fading animation
        Timeline timeline = new Timeline();
        timeline.getKeyFrames().addAll(
                new KeyFrame(Duration.millis(300),
                        new KeyValue(slideView.opacityProperty(), 0)));
        timeline.setOnFinished(event -> {
            rootGroup.getChildren().remove(slideView);
            slideEvntToView.remove(slideEvent);
        });
        timeline.play();
    }

    @Override
    public void startSliding(SlideEvent slideEvent) {
        if (slideEvntToView.containsKey(slideEvent)) {
            slideEvntToView.get(slideEvent).addAnimation(
                    new ScopeCircle(slideEvent.getStartX(),
                            slideEvent.getStartY(),
                            slideEvent.getFinishX(),
                            slideEvent.getFinishY(),
                            javafx.util.Duration.millis(slideEvent.getSlideDuration().toMillis()),
                            rootGroup));
        }
    }

    private final Group rootGroup;
    private final Group circlesGroup;

    private HashMap<TapEvent, TapView> tapEvntToView = new HashMap<>();
    private HashMap<SlideEvent, SlideView> slideEvntToView = new HashMap<>();
}

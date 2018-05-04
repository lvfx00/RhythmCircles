package ru.nsu.fit.semenov.rhythmcircles;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.BoxBlur;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import ru.nsu.fit.semenov.rhythmcircles.events.SlideEvent;
import ru.nsu.fit.semenov.rhythmcircles.events.TapEvent;
import ru.nsu.fit.semenov.rhythmcircles.views.ViewParams;

import java.time.Duration;
import java.util.concurrent.ThreadLocalRandom;

public class MainApplication extends Application {
    public static final int SCREEN_WIDTH = 1200;
    public static final int SCREEN_HEIGHT = 900;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

        Group root = new Group();
        Scene scene = new Scene(root, SCREEN_WIDTH, SCREEN_HEIGHT, Color.BLACK);
        scene.getStylesheets().add("ru/nsu/fit/semenov/rhythmcircles/style.css");
        primaryStage.setScene(scene);

        Group circlesGroup = new Group();
        circlesGroup.setEffect(new BoxBlur(10, 10, 3));

        Rectangle colors = new Rectangle(scene.getWidth(), scene.getHeight(),
                new LinearGradient(0f, 1f, 1f, 0f, true, CycleMethod.NO_CYCLE,
                        new Stop(0, Color.web("#f8bd55")),
                        new Stop(0.14, Color.web("#c0fe56")),
                        new Stop(0.28, Color.web("#5dfbc1")),
                        new Stop(0.43, Color.web("#64c2f8")),
                        new Stop(0.57, Color.web("#be4af7")),
                        new Stop(0.71, Color.web("#ed5fc2")),
                        new Stop(0.85, Color.web("#ef504c")),
                        new Stop(1, Color.web("#f2660f"))));
        colors.widthProperty().bind(scene.widthProperty());
        colors.heightProperty().bind(scene.heightProperty());


        Group tempGroup = new Group(new Rectangle(scene.getWidth(), scene.getHeight(),
                Color.BLACK), circlesGroup);

        Group blendModeGroup = new Group(colors, tempGroup);
        tempGroup.setBlendMode(BlendMode.HARD_LIGHT);
        root.getChildren().add(blendModeGroup);

        MyGameModel myGameModel = new MyGameModel();
        MyPresenter myPresenter = new MyPresenter(root, circlesGroup);
        myGameModel.registerPresenter(myPresenter);
        myGameModel.start();

        AnimationTimer animator = new AnimationTimer() {
            @Override
            public void handle(long arg0) {
                myGameModel.update();
            }
        };
        animator.start();

        primaryStage.show();
    }
}

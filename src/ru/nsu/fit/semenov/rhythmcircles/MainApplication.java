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

import java.time.Duration;

public class MainApplication extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

        Group root = new Group();
        Scene scene = new Scene(root, 800, 600, Color.BLACK);
        primaryStage.setScene(scene);

        Group circlesGroup = new Group();
        circlesGroup.setEffect(new BoxBlur(10, 10, 3));

        RhythmMap rhythmMap = new RhythmMap();
        rhythmMap.addEvent(new TapEvent(100, 100, Duration.ofSeconds(10)));
        rhythmMap.addEvent(new TapEvent(200, 200, Duration.ofSeconds(20)));
        rhythmMap.addEvent(new TapEvent(300, 300, Duration.ofSeconds(30)));
        GameModel gameModel = new GameModel(rhythmMap);
        MyPresenter myPresenter = new MyPresenter(gameModel, circlesGroup);
        gameModel.registerPresenter(myPresenter);

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

        Group blendModeGroup =
                new Group(new Group(new Rectangle(scene.getWidth(), scene.getHeight(),
                        Color.BLACK), circlesGroup), colors);
        colors.setBlendMode(BlendMode.OVERLAY);
        root.getChildren().add(blendModeGroup);

        gameModel.start();

        AnimationTimer animator = new AnimationTimer() {
            @Override
            public void handle(long arg0) {
                gameModel.update();
            }
        };
        animator.start();

        primaryStage.show();
    }
}

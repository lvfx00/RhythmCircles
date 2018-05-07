package ru.nsu.fit.semenov.rhythmcircles;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.BoxBlur;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

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

        // group for all rainbow-like elements
        Group rainbowGroup = new Group();
        rainbowGroup.setEffect(new BoxBlur(5, 5, 5));

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
                Color.BLACK), rainbowGroup);

        Group blendModeGroup = new Group(colors, tempGroup);
        tempGroup.setBlendMode(BlendMode.HARD_LIGHT);
        root.getChildren().add(blendModeGroup);

        Group mainGroup = new Group();
        root.getChildren().addAll(mainGroup);


        MyGameModel myGameModel = new MyGameModel();
        MyPresenter myPresenter = new MyPresenter(mainGroup, rainbowGroup);
        Timeline timeline = new Timeline("resources/times.txt");

        Media m = new Media("file:///" + System.getProperty("user.dir").replace('\\', '/') + "/" + "resources/osu.mp3");
        MediaPlayer player = new MediaPlayer(m);

        myGameModel.start(myPresenter, timeline);
        player.play();

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

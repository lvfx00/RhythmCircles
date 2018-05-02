package ru.nsu.fit.semenov.rhythmcircles;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.StrokeType;

public class CircleView extends Circle {
    public static final int RADIUS = 70;

    CircleView() {
        super(RADIUS, Color.web("white", 0.05));

        setStrokeType(StrokeType.OUTSIDE);
        setStroke(Color.web("white", 0.6));
        setStrokeWidth(4);
    };
}

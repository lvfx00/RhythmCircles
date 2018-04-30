package ru.nsu.fit.semenov.rhythmcircles;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.StrokeType;

public class CircleView extends Circle {
    CircleView() {
        super(100, Color.web("white", 0.05));

        setStrokeType(StrokeType.OUTSIDE);
        setStroke(Color.web("white", 0.16));
        setStrokeWidth(4);
    };
}

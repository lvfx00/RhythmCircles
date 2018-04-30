package ru.nsu.fit.semenov.rhythmcircles;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.StrokeType;

public class CircleView extends Circle {
    CircleView() {
        super(80, Color.web("white", 0.05));

        setStrokeType(StrokeType.OUTSIDE);
        setStroke(Color.web("white", 0.5));
        setStrokeWidth(4);
    };
}

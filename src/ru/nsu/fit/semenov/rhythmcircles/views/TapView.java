package ru.nsu.fit.semenov.rhythmcircles.views;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.StrokeType;

import static ru.nsu.fit.semenov.rhythmcircles.views.ViewParams.RADIUS;

public class TapView extends Group {
    public TapView(double x, double y) {
        circle = new Circle(RADIUS, Color.web("white", 0.05));

        circle.setStrokeType(StrokeType.OUTSIDE);
        circle.setStroke(Color.web("white", 0.6));
        circle.setStrokeWidth(4);

        circle.setCenterX(x);
        circle.setCenterY(y);

        super.getChildren().add(circle);
    }

    public Circle getCircle() {
        return circle;
    }

    private Circle circle;
}

package ru.nsu.fit.semenov.rhythmcircles.views;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Text;
import ru.nsu.fit.semenov.rhythmcircles.animations.Animation;
import ru.nsu.fit.semenov.rhythmcircles.animations.AnimationCarrier;

import java.util.ArrayList;

import static ru.nsu.fit.semenov.rhythmcircles.views.ViewParams.RADIUS;

public class TapView extends Group implements AnimationCarrier {
    public TapView(double x, double y) {
        circle = new Circle(RADIUS, Color.web("white", 0.15));
        circle.setStrokeType(StrokeType.OUTSIDE);
        circle.setStroke(Color.web("white", 1));
        circle.setStrokeWidth(5);
        circle.setCenterX(x);
        circle.setCenterY(y);

        super.getChildren().addAll(circle);
    }

    public Circle getCircle() {
        return circle;
    }

    @Override
    public void addAnimation(Animation animation) {
        animationList.add(animation);
        animation.run();
    }

    @Override
    public void removeAllAnimations() {
        for(Animation an : animationList) {
            if(!an.finished()) {
                an.stop();
            }
        }
        animationList.clear();
    }

    private ArrayList<Animation> animationList = new ArrayList<>();
    private Text numberText;
    private Circle circle;
}

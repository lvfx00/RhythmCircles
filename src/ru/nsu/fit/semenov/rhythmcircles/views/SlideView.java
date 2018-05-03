package ru.nsu.fit.semenov.rhythmcircles.views;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import ru.nsu.fit.semenov.rhythmcircles.views.animations.Animation;
import ru.nsu.fit.semenov.rhythmcircles.views.animations.AnimationCarrier;

import java.util.ArrayList;

import static ru.nsu.fit.semenov.rhythmcircles.views.ViewParams.RADIUS;

public class SlideView extends Group implements AnimationCarrier {

    public SlideView(double x1, double y1, double x2, double y2) {
        startCircle = new Circle(RADIUS, Color.web("white", 0.05));
        finishCircle = new Circle(RADIUS, Color.web("white", 0.05));

        startCircle.setStrokeType(StrokeType.OUTSIDE);
        startCircle.setStroke(Color.web("white", 0.6));
        startCircle.setStrokeWidth(4);

        finishCircle.setStrokeType(StrokeType.OUTSIDE);
        finishCircle.setStroke(Color.web("white", 0.6));
        finishCircle.setStrokeWidth(4);

        startCircle.setCenterX(x1);
        startCircle.setCenterY(y1);
        finishCircle.setCenterX(x2);
        finishCircle.setCenterY(y2);

        double rectWidth = Math.sqrt(Math.pow(x2 - x1, 2.0) + Math.pow(y2 - y1, 2.0));
        double rectHeight = 2 * ViewParams.RADIUS;

        pathRectangle = new Rectangle(rectWidth, rectHeight, Color.web("white", 0.05));

        pathRectangle.setStrokeType(StrokeType.OUTSIDE);
        pathRectangle.setStroke(Color.web("white", 0.6));
        pathRectangle.setStrokeWidth(4);

        // set center of rectangle to path center
        double rectCenterX = (x1 + x2) / 2;
        double rectCenterY = (y1 + y2) / 2;
        pathRectangle.setX(rectCenterX - rectWidth / 2);
        pathRectangle.setY(rectCenterY - rectHeight / 2);

        // rotate rectangle
        double rotationDegree = Math.toDegrees(Math.atan((y2 - y1) / (x2 - x1)));
        pathRectangle.setRotate(rotationDegree);

        super.getChildren().addAll(pathRectangle, startCircle, finishCircle);
    }

    public Circle getStartCircle() {
        return startCircle;
    }

    public Circle getFinishCircle() {
        return finishCircle;
    }

    public Rectangle getPathRectangle() {
        return pathRectangle;
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

    private Circle startCircle;
    private Circle finishCircle;
    private Rectangle pathRectangle;

}


package ru.nsu.fit.semenov.rhythmcircles.views;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Text;
import javafx.util.Duration;
import ru.nsu.fit.semenov.rhythmcircles.animations.Animation;
import ru.nsu.fit.semenov.rhythmcircles.animations.AnimationCarrier;

import java.util.ArrayList;

import static ru.nsu.fit.semenov.rhythmcircles.views.ViewParams.RADIUS;

public class SlideView extends Group implements AnimationCarrier {

    public SlideView(double x1, double y1, double x2, double y2, java.time.Duration slidingDuration) {
        this.slidingDuration = Duration.millis(slidingDuration.toMillis());

        startCircle = new Circle(RADIUS, Color.web("white", 0.15));
        startCircle.setStrokeType(StrokeType.OUTSIDE);
        startCircle.setStroke(Color.web("white", 1));
        startCircle.setStrokeWidth(5);
        startCircle.setCenterX(x1);
        startCircle.setCenterY(y1);

        finishCircle = new Circle(RADIUS, Color.web("white", 0.15));
        finishCircle.setStrokeType(StrokeType.OUTSIDE);
        finishCircle.setStroke(Color.web("white", 1));
        finishCircle.setStrokeWidth(5);
        finishCircle.setCenterX(x2);
        finishCircle.setCenterY(y2);

//        double rectWidth = Math.sqrt(Math.pow(x2 - x1, 2.0) + Math.pow(y2 - y1, 2.0));
//        double rectHeight = 2 * ViewParams.RADIUS;
//        pathRectangle = new Rectangle(rectWidth, rectHeight, Color.web("white", 0.05));
//        pathRectangle.setStrokeType(StrokeType.OUTSIDE);
//        pathRectangle.setStroke(Color.web("white", 1));
//        pathRectangle.setStrokeWidth(5);
//
//        // set center of rectangle to path center
//        double rectCenterX = (x1 + x2) / 2;
//        double rectCenterY = (y1 + y2) / 2;
//        pathRectangle.setX(rectCenterX - rectWidth / 2);
//        pathRectangle.setY(rectCenterY - rectHeight / 2);
//
//        // rotate rectangle
//        pathRectangle.setRotate(Math.toDegrees(rotationDegree));

        double rotationDegree = Math.atan((y2 - y1) / (x2 - x1));
        double dx = Math.sin(rotationDegree) * (RADIUS + 2.5);
        double dy = Math.cos(rotationDegree) * (RADIUS + 2.5);

        Line firstLine = new Line(x1 + dx, y1 - dy, x2 + dx, y2 - dy);
        firstLine.setFill(Color.web("white", 0.05));
        firstLine.setStrokeType(StrokeType.OUTSIDE);
        firstLine.setStroke(Color.web("white", 1));
        firstLine.setStrokeWidth(2.5);

        Line secondLine = new Line(x1 - dx, y1 + dy, x2 - dx, y2 + dy);
        secondLine.setStrokeType(StrokeType.OUTSIDE);
        secondLine.setStroke(Color.web("white", 1));
        secondLine.setStrokeWidth(2.5);

        super.getChildren().addAll(startCircle, finishCircle, firstLine, secondLine);

        // scope animation
        innerScopeCircle = new Circle(RADIUS, Color.web("white", 0));
        innerScopeCircle.setStrokeType(StrokeType.OUTSIDE);
        innerScopeCircle.setStroke(Color.web("white", 1));
        innerScopeCircle.setStrokeWidth(5);
        innerScopeCircle.setCenterX(x1);
        innerScopeCircle.setCenterY(y1);
        innerScopeCircle.setMouseTransparent(true);

        outerScopeCircle = new Circle(RADIUS + 30, Color.web("white", 0));
        outerScopeCircle.setStrokeType(StrokeType.OUTSIDE);
        outerScopeCircle.setStroke(Color.web("white", 1));
        outerScopeCircle.setStrokeWidth(5);
        outerScopeCircle.setCenterX(x1);
        outerScopeCircle.setCenterY(y1);
        outerScopeCircle.setMouseTransparent(true);

        // add moving circle
        movingCircle = new Circle(RADIUS, Color.web("white", 0));
        movingCircle.setStrokeType(StrokeType.OUTSIDE);
        movingCircle.setStroke(Color.web("white", 1));
        movingCircle.setStrokeWidth(5);
        movingCircle.setCenterX(x1);
        movingCircle.setCenterY(y1);

        super.getChildren().addAll(movingCircle);
    }

    public Circle getStartCircle() {
        return startCircle;
    }

    public Circle getFinishCircle() {
        return finishCircle;
    }

    public Circle getMovingCircle() {
        return movingCircle;
    }

    public Rectangle getPathRectangle() {
        return pathRectangle;
    }

    public double getScopeCircleX() {
        return innerScopeCircle.getCenterX();
    }

    public double getScopeCircleY() {
        return innerScopeCircle.getCenterY();
    }

    public void startSlidingAnimation() {
        super.getChildren().addAll(innerScopeCircle);
        super.getChildren().addAll(outerScopeCircle);

        Timeline timeline = new Timeline();
        timeline.getKeyFrames().addAll(
                new KeyFrame(slidingDuration,
                        new KeyValue(outerScopeCircle.centerXProperty(), finishCircle.getCenterX()),
                        new KeyValue(outerScopeCircle.centerYProperty(), finishCircle.getCenterY()),
                        new KeyValue(innerScopeCircle.centerXProperty(), finishCircle.getCenterX()),
                        new KeyValue(innerScopeCircle.centerYProperty(), finishCircle.getCenterY())));

        timeline.setOnFinished(event -> {
            Timeline stopTimeline = new Timeline();
            stopTimeline.getKeyFrames().addAll(
                    new KeyFrame(Duration.millis(300),
                            new KeyValue(outerScopeCircle.opacityProperty(), 0),
                            new KeyValue(innerScopeCircle.opacityProperty(), 0)));

            stopTimeline.setOnFinished(event2 -> {
                getChildren().remove(innerScopeCircle);
                getChildren().remove(outerScopeCircle);
            });
            stopTimeline.play();
        });
        timeline.play();


        Timeline life = new Timeline();
        life.getKeyFrames().addAll(new KeyFrame(slidingDuration));
        life.setOnFinished(event -> super.getChildren().remove(movingCircle));
        life.play();

    }

    @Override
    public void addAnimation(Animation animation) {
        animationList.add(animation);
        animation.run();
    }

    @Override
    public void removeAllAnimations() {
        for (Animation an : animationList) {
            if (!an.finished()) {
                an.stop();
            }
        }
        animationList.clear();
    }

    private ArrayList<Animation> animationList = new ArrayList<>();

    private Circle startCircle;
    private Circle finishCircle;
    private Circle innerScopeCircle;
    private Circle outerScopeCircle;
    private Circle movingCircle;
    private Rectangle pathRectangle;
    private Text numberText;
    private Duration slidingDuration;
}

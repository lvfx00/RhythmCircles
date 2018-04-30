package ru.nsu.fit.semenov.rhythmcircles;

import javafx.scene.Group;

import java.util.HashMap;

public class MyPresenter implements GamePresenter {
    public MyPresenter(GameModel gm, Group cg) {
        gameModel = gm;
        circlesGroup = cg;
    }

    @Override
    public void addEventView(GameEvent ge) {
        switch (ge.getEventType()) {
            case TAP:
                CircleView circle = new CircleView();
                circle.relocate(((TapEvent)ge).getX(), ((TapEvent)ge).getY());

                // TODO add showing animation
                circlesGroup.getChildren().add(circle);

                evntToView.put((TapEvent)ge, circle);
                viewToEvnt.put(circle, (TapEvent)ge);
                break;
        }
    }

    @Override
    public void removeEventView(GameEvent ge) {
        switch (ge.getEventType()) {
            case TAP:
                CircleView circle = evntToView.get((TapEvent)ge);

                // TODO add disappearing animation
                circlesGroup.getChildren().remove(circle);

                evntToView.remove((TapEvent)ge);
                viewToEvnt.remove(circle);
                break;
        }
    }

    private final Group circlesGroup;
    private final GameModel gameModel;
    private HashMap<TapEvent, CircleView> evntToView = new HashMap<>();
    private HashMap<CircleView, TapEvent> viewToEvnt = new HashMap<>();
}

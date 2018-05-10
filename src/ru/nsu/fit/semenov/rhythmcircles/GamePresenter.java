package ru.nsu.fit.semenov.rhythmcircles;

import ru.nsu.fit.semenov.rhythmcircles.events.SlideEvent;
import ru.nsu.fit.semenov.rhythmcircles.events.TapEvent;

public interface GamePresenter {
    void add(TapEvent event, int orderingNum);

    void start(TapEvent event);

    void remove(TapEvent event);

    void add(SlideEvent event, int orderingNum);

    void start(SlideEvent event);

    void remove(SlideEvent event);

    void startSliding(SlideEvent event);

    void pulse(double x, double y);
}

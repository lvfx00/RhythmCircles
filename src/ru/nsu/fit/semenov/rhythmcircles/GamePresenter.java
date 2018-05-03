package ru.nsu.fit.semenov.rhythmcircles;

import ru.nsu.fit.semenov.rhythmcircles.events.SlideEvent;
import ru.nsu.fit.semenov.rhythmcircles.events.TapEvent;

public interface GamePresenter {
    void addTapEventView(TapEvent tapEvent);

    void removeTapEventView(TapEvent tapEvent);

    void addSlideEventView(SlideEvent slideEvent);

    void removeSlideEventView(SlideEvent slideEvent);

    void startSliding(SlideEvent slideEvent);

    // trigger pulse animation for successful check points
    void pulse(SlideEvent slideEvent);

}

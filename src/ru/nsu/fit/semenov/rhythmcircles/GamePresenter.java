package ru.nsu.fit.semenov.rhythmcircles;

import ru.nsu.fit.semenov.rhythmcircles.events.SlideEvent;
import ru.nsu.fit.semenov.rhythmcircles.events.TapEvent;

public interface GamePresenter {
    void addTapEventView(TapEvent tapEvent);

    void startTapEvent(TapEvent tapEvent);

    void removeTapEventView(TapEvent tapEvent);

    void addSlideEventView(SlideEvent slideEvent);

    void startSlideEvent(SlideEvent slideEvent);

    void removeSlideEventView(SlideEvent slideEvent);

    void startSliding(SlideEvent slideEvent);
}

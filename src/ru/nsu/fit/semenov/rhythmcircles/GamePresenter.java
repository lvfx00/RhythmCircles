package ru.nsu.fit.semenov.rhythmcircles;

import ru.nsu.fit.semenov.rhythmcircles.events.GameEvent;

public interface GamePresenter {
    void addEventView(GameEvent ge);

    void removeEventView(GameEvent ge);

    void showScores(GameEvent ge, int sores);
}

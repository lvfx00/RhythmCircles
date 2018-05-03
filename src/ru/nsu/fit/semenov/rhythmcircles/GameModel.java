package ru.nsu.fit.semenov.rhythmcircles;

import java.util.Set;

public interface GameModel {
    void start();

    void update();

    void registerPresenter(GamePresenter gamePresenter);

    void submitTask(Runnable r);

    Set<GamePresenter> getPresentersView();
}

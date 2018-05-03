package ru.nsu.fit.semenov.rhythmcircles;

public interface GameModel {
    void start();

    void update();

    void registerPresenter(GamePresenter gamePresenter);
}

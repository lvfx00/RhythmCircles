package ru.nsu.fit.semenov.rhythmcircles;

import java.util.function.Consumer;

public interface GameModel {
    void start();

    void update();

    void registerPresenter(GamePresenter gamePresenter);

    void submitTask(Consumer<GamePresenter> cons);
}

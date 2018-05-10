package ru.nsu.fit.semenov.rhythmcircles;

import java.util.function.Consumer;

public interface GameModel {
    void start(GamePresenter gp, TimeMap timeMap);

    void update();

    void submitEventTask(Consumer<GamePresenter> cons);
}

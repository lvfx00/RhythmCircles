package ru.nsu.fit.semenov.rhythmcircles;

import java.util.function.Consumer;

public interface GameModel {
    void start(GamePresenter gp, Timeline timeline);

    void update();

    void submitEventTask(Consumer<GamePresenter> cons);
}

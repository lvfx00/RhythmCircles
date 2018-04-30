package ru.nsu.fit.semenov.rhythmcircles;

import ru.nsu.fit.semenov.rhythmcircles.events.GameEvent;

import java.time.Duration;
import java.util.LinkedList;

class RhythmMap {
    boolean hasNext(Duration time) {
        return !eventDeque.isEmpty() &&
                eventDeque.peek().getBeginningTime().compareTo(time) < 0;
    }

    GameEvent getNext() {
        return eventDeque.pop();
    }

    void addEvent(GameEvent ge) {
        eventDeque.add(ge);
    }

    private LinkedList<GameEvent> eventDeque = new LinkedList<>();
}

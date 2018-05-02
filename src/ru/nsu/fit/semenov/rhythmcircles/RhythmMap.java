package ru.nsu.fit.semenov.rhythmcircles;

import org.jetbrains.annotations.NotNull;
import ru.nsu.fit.semenov.rhythmcircles.events.GameEvent;

import java.time.Duration;
import java.util.LinkedList;

class RhythmMap {

    void addEvent(GameEvent gameEvent, Duration beginningTime) {
        eventList.add(new Pair<>(gameEvent, beginningTime));
    }

    boolean available(Duration currTime) {
        if (eventList.isEmpty()) {
            return false;
        }
        return eventList.peek().getRight().compareTo(currTime) < 0;
    }

    GameEvent getNextEvent() {
        return eventList.pop().getLeft();
    }

    boolean empty() {
        return eventList.isEmpty();
    }


    private LinkedList<Pair<GameEvent, Duration>> eventList = new LinkedList<>();
}


class Pair<L,R> {

    private final L left;
    private final R right;

    public Pair(@NotNull L left, @NotNull R right) {
        this.left = left;
        this.right = right;
    }

    public @NotNull L getLeft() { return left; }
    public @NotNull R getRight() { return right; }

    @Override
    public int hashCode() { return left.hashCode() ^ right.hashCode(); }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Pair)) return false;
        Pair pairo = (Pair) o;
        return this.left.equals(pairo.getLeft()) &&
                this.right.equals(pairo.getRight());
    }

}

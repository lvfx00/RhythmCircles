package ru.nsu.fit.semenov.rhythmcircles.events;

import javafx.geometry.Bounds;
import org.jetbrains.annotations.NotNull;

public class EventBounds {
    public EventBounds(@NotNull Bounds b) {
        bounds = b;
    }

    public boolean intersects(EventBounds eb) {
        return bounds.intersects(eb.bounds);
    }

    private Bounds bounds;
}

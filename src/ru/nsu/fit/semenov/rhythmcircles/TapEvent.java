package ru.nsu.fit.semenov.rhythmcircles;

import java.time.Duration;

import static ru.nsu.fit.semenov.rhythmcircles.EventType.TAP;

public class TapEvent implements GameEvent {
    public static final Duration eventDuration = Duration.ofMillis(3500);
    public static final Duration idealTime = Duration.ofMillis(500);
    public static final Duration regularTime = Duration.ofMillis(1500);

    TapEvent(double x, double y, Duration time) {
        this.x = x;
        this.y = y;
        eventTime = time;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public Duration getEventTime() {
        return eventTime;
    }

    @Override
    public EventType getEventType() {
        return TAP;
    }

    @Override
    public Duration getBeginningTime() {
        return eventTime;
    }

    @Override
    public Duration getEventDuration() {
        return eventDuration;
    }

    @Override
    public boolean finished(Duration time) {
        return eventTime.plus(eventDuration).compareTo(time) < 0;
    }

    private final double x;
    private final double y;
    private Duration eventTime;
}

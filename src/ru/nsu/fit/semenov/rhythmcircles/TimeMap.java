package ru.nsu.fit.semenov.rhythmcircles;

import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.LinkedList;

public class TimeMap {
    TimeMap(@NotNull Duration offset, @NotNull Duration bitrate, @NotNull Duration songDuration) {
        this.bitrate = bitrate;

        Duration next = offset;
        while (next.compareTo(songDuration) < 0) {
            timeList.add(next);
            next = next.plus(bitrate);
        }
    }

    public boolean isEmpty() {
        return timeList.isEmpty();
    }

    public boolean hasNext(Duration currTime) {
        return !timeList.isEmpty() && timeList.peekFirst().compareTo(currTime) < 0;
    }

    public Duration getNext() {
        return timeList.pop();
    }

    public Duration getBitrate() {
        return bitrate;
    }

    private LinkedList<Duration> timeList = new LinkedList<>();
    private final Duration bitrate;
}

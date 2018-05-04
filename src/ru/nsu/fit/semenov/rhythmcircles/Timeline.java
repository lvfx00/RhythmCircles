package ru.nsu.fit.semenov.rhythmcircles;

import java.io.*;
import java.time.Duration;
import java.util.LinkedList;

// must be sorted
public class Timeline {
    public Timeline(String filename) {
        File file = new File(filename);
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String text;

            int a = 0;
            while ((text = reader.readLine()) != null) {
                if (a % 3 == 0) {
                    timeList.add((long) Double.parseDouble(text));
                }
                a++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isEmpty() {
        return timeList.isEmpty();
    }

    public boolean hasTwo(Duration currTime) {
        return timeList.size() > 1 &&
                Duration.ofMillis(timeList.peekFirst()).compareTo(currTime) < 0 &&
                Duration.ofMillis(timeList.get(1)).compareTo(currTime) < 0;
    }

    public boolean hasNext(Duration currTime) {
        return !timeList.isEmpty() && Duration.ofMillis(timeList.peekFirst()).compareTo(currTime) < 0;
    }

    public Duration getNext() {
        return Duration.ofMillis(timeList.pop());
    }

    private LinkedList<Long> timeList = new LinkedList<>();
}

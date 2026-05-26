import java.awt.*;
import java.util.Random;

private class Philosopher implements Runnable {
    private final int id;
    private final Random random = new Random();
    private int mealsEaten = 0;

    public Philosopher(int id) {
        this.id = id;
        updateState("Thinking", Color.GREEN);
    }

    private void updateState(String stateText, Color color) {
        states[id] = stateText;
        philosopherColors[id] = color;
        repaint();
    }
}
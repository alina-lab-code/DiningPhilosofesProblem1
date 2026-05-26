import java.awt.*;
import java.util.Random;

import java.awt.Color;
import java.util.Random;


public class Philosopher implements Runnable {
    private final int id;
    private final MainScene panel;
    private final Random random = new Random();
    private int mealsEaten = 0;


    // Конструктор (он у вас правильный)
    public Philosopher(int id, MainScene panel) {
        this.id = id;
        this.panel = panel;
        updateState("Thinking", Color.GREEN);
    }

    // Вот этот метод updateState нужно полностью переписать вот так:
    private void updateState(String stateText, Color color) {
        panel.states[id] = stateText;
        panel.philosopherColors[id] = color;
        panel.repaint();

    }

    @Override
    public void run() {

    }
}
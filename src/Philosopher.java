import java.awt.*;
import java.util.Random;

import java.awt.Color;
import java.util.Random;


public class Philosopher implements Runnable {
    private final int id;
    private final MainScene panel;
    private final Random random = new Random();
    private int mealsEaten = 0;



    public Philosopher(int id, MainScene panel) {
        this.id = id;
        this.panel = panel;
        updateState("Thinking", Color.GREEN);
    }


    private void updateState(String stateText, Color color) {
        panel.states[id] = stateText;
        panel.philosopherColors[id] = color;
        panel.repaint();

    }

    @Override
    public void run() {
        try {
            while (true) {

                updateState("Thinking", Color.GREEN);
                Thread.sleep(random.nextInt(5000));


                int leftFork = id;
                int rightFork = (id + 1) % NUM_PHILOSOPHERS;


                updateState("Waiting for 1st fork", Color.ORANGE);


                if (id == NUM_PHILOSOPHERS - 1) {
                    pickUpFork(rightFork);

                    updateState("Waiting for 2nd fork", Color.ORANGE);
                    Thread.sleep(random.nextInt(1000));
                    pickUpFork(leftFork);
                } else {
                    pickUpFork(leftFork);
                    updateState("Waiting for 2nd fork", Color.ORANGE);
                    Thread.sleep(random.nextInt(1000));
                    pickUpFork(rightFork);
                }
    }
}
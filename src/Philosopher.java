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
                    int rightFork = (id + 1) % panel.NUM_PHILOSOPHERS;


                    updateState("Waiting for 1st fork", Color.ORANGE);


                    if (id == panel.NUM_PHILOSOPHERS - 1) {

                        panel.forks[rightFork].lock();

                        updateState("Waiting for 2nd fork", Color.ORANGE);
                        Thread.sleep(random.nextInt(1000));

                        panel.forks[leftFork].lock();
                    } else {

                        panel.forks[leftFork].lock();

                        updateState("Waiting for 2nd fork", Color.ORANGE);
                        Thread.sleep(random.nextInt(1000));


                        panel.forks[rightFork].lock();
                    }


                    mealsEaten++;

                    updateState("Eating (" + mealsEaten + ")", Color.RED);
                    Thread.sleep(random.nextInt(2000) + 1000);


                    panel.forks[leftFork].unlock();
                    panel.forks[rightFork].unlock();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

    }

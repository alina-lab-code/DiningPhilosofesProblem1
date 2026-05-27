import java.awt.*;
import java.util.Random;

import java.awt.Color;
import java.util.Random;


import java.awt.Color;
import java.util.Random;

import java.awt.Color;
import java.util.Random;

public class Philosopher implements Runnable {
    private final int id;
    private final MainScene panel;
    private final Random random = new Random();
    private int mealsEaten = 0;

    // Флаг управления жизненным циклом потока
    private volatile boolean running = true;

    public Philosopher(int id, MainScene panel) {
        this.id = id;
        this.panel = panel;
        updateState("Thinking", Color.GREEN);
    }

    public void stopPhilosopher() {
        this.running = false;
    }

    public void startPhilosopher() {
        this.running = true;
    }

    private void updateState(String stateText, Color color) {
        if (id < panel.states.length) {
            panel.states[id] = stateText;
            panel.philosopherColors[id] = color;
            panel.repaint();
        }
    }

    @Override
    public void run() {
        try {
            while (running) {
                // 1. Философ думает
                updateState("Thinking", Color.GREEN);
                Thread.sleep(random.nextInt(5000));

                if (!running) break;

                // Определяем вилки на основе текущего динамического размера стола
                int leftFork = id;
                int totalForks = panel.getForksCount();
                int rightFork = (id + 1) % totalForks;

                updateState("Waiting for 1st fork", Color.ORANGE);

                // Предотвращение Deadlock через асимметрию для последнего за столом
                if (id == totalForks - 1) {
                    panel.lockFork(rightFork);
                    updateState("Waiting for 2nd fork", Color.ORANGE);
                    Thread.sleep(random.nextInt(1000));
                    panel.lockFork(leftFork);
                } else {
                    panel.lockFork(leftFork);
                    updateState("Waiting for 2nd fork", Color.ORANGE);
                    Thread.sleep(random.nextInt(1000));
                    panel.lockFork(rightFork);
                }

                // Экстренный выход, если во время ожидания пришел сигнал остановки
                if (!running) {
                    panel.unlockFork(leftFork);
                    panel.unlockFork(rightFork);
                    break;
                }

                // 2. Философ ест
                mealsEaten++;
                if (id < panel.eatCounters.length) {
                    panel.eatCounters[id] = mealsEaten;
                }
                updateState("Eating (" + mealsEaten + ")", Color.RED);
                Thread.sleep(random.nextInt(2000) + 1000);

                // Освобождение ресурсов
                panel.unlockFork(leftFork);
                panel.unlockFork(rightFork);
            }

            // Отображение конечного состояния при остановке
            updateState("Stopped (" + mealsEaten + ")", Color.GRAY);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
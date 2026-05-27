import java.awt.*;
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

    // Флаг для безопасной остановки потока
    private volatile boolean running = true;

    public Philosopher(int id, MainScene panel) {
        this.id = id;
        this.panel = panel;
        updateState("Thinking", Color.GREEN);
    }

    public int getId() {
        return id;
    }

    public void stopPhilosopher() {
        this.running = false;
    }

    private void updateState(String stateText, Color color) {
        // Проверяем границы массива на случай, если количество элементов изменилось
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

                // Вычисляем вилки динамически на основе текущего размера списка в panel
                int leftFork = id;
                // Правая вилка — это следующая вилка по кругу
                int totalForks = panel.getForksCount();
                int rightFork = (id + 1) % totalForks;

                updateState("Waiting for 1st fork", Color.ORANGE);

                // Асимметричное решение проблемы взаимной блокировки (Deadlock)
                // Если это последний философ в текущем кругу, он берет сначала правую вилку
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

                if (!running) {
                    // Если во время ожидания пришёл сигнал остановки, освобождаем вилки и выходим
                    panel.unlockFork(leftFork);
                    panel.unlockFork(rightFork);
                    break;
                }

                // 2. Философ ест
                mealsEaten++;
                // Обновляем счётчик в панели (из задания: вывести сколько раз удалось поесть)
                panel.eatCounters[id] = mealsEaten;
                updateState("Eating (" + mealsEaten + ")", Color.RED);
                Thread.sleep(random.nextInt(2000) + 1000);

                // Освобождаем ресурсы
                panel.unlockFork(leftFork);
                panel.unlockFork(rightFork);
            }

            // Состояние после остановки потока
            updateState("Stopped (" + mealsEaten + ")", Color.GRAY);

        } catch (InterruptedException e) {
            // В случае прерывания освобождаем ресурсы, если они были захвачены
            Thread.currentThread().interrupt();
        }
    }
}
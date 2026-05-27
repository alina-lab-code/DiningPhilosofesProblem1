import javax.swing.*;
import java.awt.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MainScene extends JPanel {

    protected int NUM_PHILOSOPHERS = 5;

    protected final int MAX_PHILOSOPHERS = 7;


    protected final Philosopher[] philosophers = new Philosopher[MAX_PHILOSOPHERS];
    protected final Thread[] threads = new Thread[MAX_PHILOSOPHERS];


    protected final Lock[] forks = new ReentrantLock[MAX_PHILOSOPHERS];

    // Массивы для графического отображения состояния (сделаны protected)
    protected final String[] states = new String[MAX_PHILOSOPHERS];
    protected final Color[] philosopherColors = new Color[MAX_PHILOSOPHERS];
    protected final Color[] forkColors = new Color[MAX_PHILOSOPHERS];
    protected final int[] eatCounters = new int[MAX_PHILOSOPHERS];

    public MainScene() {
        setPreferredSize(new java.awt.Dimension(800, 600));
        setBackground(Color.WHITE);
        setLayout(new BorderLayout()); // Позволяет разместить панель управления сверху

        // 1. Инициализируем базовые вилки (первые 5 штук)
        for (int i = 0; i < NUM_PHILOSOPHERS; i++) {
            forks[i] = new ReentrantLock();
            forkColors[i] = Color.BLACK; // Черный цвет — вилка свободна
        }

        // 2. Создаем верхнюю панель с кнопками управления
        JPanel controlPanel = new JPanel();
        controlPanel.setBackground(new Color(235, 240, 245));

        JButton btnStop = new JButton("Остановить философа");
        JButton btnResume = new JButton("Возобновить философа");
        JButton btnAdd = new JButton("Добавить философа (макс +2)");

        controlPanel.add(btnStop);
        controlPanel.add(btnResume);
        controlPanel.add(btnAdd);
        add(controlPanel, BorderLayout.NORTH);

        // --- ЛОГИКА КНОПКИ «ОСТАНОВИТЬ» ---
        btnStop.addActionListener(e -> {
            String input = JOptionPane.showInputDialog(this, "Введите ID философа для остановки (0 до " + (NUM_PHILOSOPHERS - 1) + "):");
            try {
                if (input != null) {
                    int id = Integer.parseInt(input.trim());
                    if (id >= 0 && id < NUM_PHILOSOPHERS) {
                        if (philosophers[id] != null) {
                            philosophers[id].stopPhilosopher();
                            JOptionPane.showMessageDialog(this, "Сигнал остановки отправлен Философу " + id);
                        }
                    } else {
                        JOptionPane.showMessageDialog(this, "Философа с таким ID не существует!");
                    }
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Введите корректное число!");
            }
        });

        // --- ЛОГИКА КНОПКИ «ВОЗОБНОВИТЬ» ---
        btnResume.addActionListener(e -> {
            String input = JOptionPane.showInputDialog(this, "Введите ID философа для возобновления (0 до " + (NUM_PHILOSOPHERS - 1) + "):");
            try {
                if (input != null) {
                    int id = Integer.parseInt(input.trim());
                    if (id >= 0 && id < NUM_PHILOSOPHERS) {
                        if (philosophers[id] != null) {
                            // Если поток умер или еще не создавался, запускаем заново
                            if (threads[id] == null || !threads[id].isAlive()) {
                                philosophers[id].startPhilosopher(); // Возвращаем running = true
                                threads[id] = new Thread(philosophers[id]); // Создаем новый поток на том же объекте
                                threads[id].start();
                                JOptionPane.showMessageDialog(this, "Философ " + id + " возобновил работу!");
                            } else {
                                JOptionPane.showMessageDialog(this, "Философ " + id + " уже работает.");
                            }
                        }
                    } else {
                        JOptionPane.showMessageDialog(this, "Неверный ID!");
                    }
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Введите корректное число!");
            }
        });

        // --- ЛОГИКА КНОПКИ «ДОБАВИТЬ ФИЛОСОФА» ---
        btnAdd.addActionListener(e -> {
            if (NUM_PHILOSOPHERS >= MAX_PHILOSOPHERS) {
                JOptionPane.showMessageDialog(this, "Достигнут лимит задания! Нельзя добавить больше 2 философов.");
                return;
            }

            int newId = NUM_PHILOSOPHERS;

            // Инициализируем замок-вилку для нового участника
            forks[newId] = new ReentrantLock();
            forkColors[newId] = Color.BLACK;

            // Создаем и запускаем философа
            philosophers[newId] = new Philosopher(newId, this);
            threads[newId] = new Thread(philosophers[newId]);

            NUM_PHILOSOPHERS++; // Увеличиваем общий счетчик круга
            threads[newId].start();

            repaint();
            JOptionPane.showMessageDialog(this, "Успешно добавлен Философ " + newId + " и его правая вилка!");
        });

        // 3. Первоначальный запуск первых 5 философов при старте программы
        for (int i = 0; i < NUM_PHILOSOPHERS; i++) {
            philosophers[i] = new Philosopher(i, this);
            threads[i] = new Thread(philosophers[i]);
            threads[i].start();
        }
    }

    // Методы взаимодействия с замками
    public int getForksCount() {
        return NUM_PHILOSOPHERS;
    }

    public void lockFork(int index) {
        forks[index].lock();
        forkColors[index] = Color.BLUE; // Синий — вилка удерживается
        repaint();
    }

    public void unlockFork(int index) {
        forkColors[index] = Color.BLACK; // Черный — свободна
        forks[index].unlock();
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2 + 20; // Небольшой сдвиг вниз из-за панели кнопок
        int radius = 150;

        int numToDraw = NUM_PHILOSOPHERS;

        for (int i = 0; i < numToDraw; i++) {
            // Динамический расчет углов под текущее количество философов за столом
            double angle = 2 * Math.PI * i / numToDraw;
            int x = centerX + (int) (radius * Math.cos(angle)) - 30;
            int y = centerY + (int) (radius * Math.sin(angle)) - 30;

            // Отрисовка философа
            g2.setColor(philosopherColors[i] != null ? philosopherColors[i] : Color.GREEN);
            g2.fillOval(x, y, 60, 60);
            g2.setColor(Color.BLACK);
            g2.drawOval(x, y, 60, 60);

            // Текстовые статусы
            g2.drawString("Philosopher " + i, x + 3, y - 22);
            String status = states[i] != null ? states[i] : "Thinking";
            g2.drawString(status, x - 5, y - 5);

            // Вывод счетчика еды (Требование ТЗ!)
            g2.drawString("Meals: " + eatCounters[i], x + 5, y + 75);

            // Динамический расчет и отрисовка положения вилок
            double forkAngle = 2 * Math.PI * (i - 0.5) / numToDraw;
            int fx1 = centerX + (int) ((radius - 40) * Math.cos(forkAngle));
            int fy1 = centerY + (int) ((radius - 40) * Math.sin(forkAngle));
            int fx2 = centerX + (int) ((radius + 10) * Math.cos(forkAngle));
            int fy2 = centerY + (int) ((radius + 10) * Math.sin(forkAngle));

            g2.setColor(forkColors[i] != null ? forkColors[i] : Color.BLACK);
            g2.setStroke(new BasicStroke(3));
            g2.drawLine(fx1, fy1, fx2, fy2);
        }
    }
}
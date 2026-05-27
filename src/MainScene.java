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

public class MainScene extends JPanel {
    private static final int INITIAL_PHILOSOPHERS = 5;
    private static final int MAX_PHILOSOPHERS = INITIAL_PHILOSOPHERS + 2; // Максимум +2 философа по заданию

    private int currentNumPhilosophers = INITIAL_PHILOSOPHERS;

    // Списки и массивы делаем динамически расширяемыми или с запасом под MAX_PHILOSOPHERS
    protected final ArrayList<Philosopher> philosophers = new ArrayList<>();
    protected final ArrayList<Thread> threads = new ArrayList<>();
    protected final ArrayList<Lock> forks = new ArrayList<>();

    // Массивы состояний подстраиваем под максимальный размер для избежания OutOfBounds
    protected String[] states = new String[MAX_PHILOSOPHERS];
    protected Color[] philosopherColors = new Color[MAX_PHILOSOPHERS];
    protected Color[] forkColors = new Color[MAX_PHILOSOPHERS];
    protected int[] eatCounters = new int[MAX_PHILOSOPHERS];

    public MainScene() {
        setPreferredSize(new java.awt.Dimension(800, 600));
        setBackground(Color.WHITE);
        setLayout(new BorderLayout()); // Используем BorderLayout, чтобы аккуратно поместить кнопки наверх

        // 1. Инициализируем первые 5 вилок и философов
        for (int i = 0; i < currentNumPhilosophers; i++) {
            forks.add(new ReentrantLock());
            forkColors[i] = Color.BLACK;
        }

        // 2. Создаем кнопки управления
        JPanel controlPanel = new JPanel();
        controlPanel.setBackground(new Color(240, 240, 240));

        JButton btnStop = new JButton("Остановить философа");
        JButton btnAdd = new JButton("Добавить философа (макс +2)");

        controlPanel.add(btnStop);
        controlPanel.add(btnAdd);
        add(controlPanel, BorderLayout.NORTH);

        // Слушатель для остановки философа
        btnStop.addActionListener(e -> {
            synchronized (philosophers) {
                if (!philosophers.isEmpty()) {
                    // Останавливаем последнего добавленного/активного философа
                    int indexToStop = philosophers.size() - 1;
                    // Ищем последнего работающего
                    while (indexToStop >= 0 && states[indexToStop].startsWith("Stopped")) {
                        indexToStop--;
                    }
                    if (indexToStop >= 0) {
                        philosophers.get(indexToStop).stopPhilosopher();
                        JOptionPane.showMessageDialog(this, "Философ " + indexToStop + " останавливается.");
                    } else {
                        JOptionPane.showMessageDialog(this, "Все философы уже остановлены.");
                    }
                }
            }
        });

        // Слушатель для добавления философа (до +2)
        btnAdd.addActionListener(e -> {
            synchronized (philosophers) {
                if (currentNumPhilosophers >= MAX_PHILOSOPHERS) {
                    JOptionPane.showMessageDialog(this, "Нельзя добавить больше 2 философов (максимум " + MAX_PHILOSOPHERS + ")!");
                    return;
                }

                int newId = currentNumPhilosophers;
                currentNumPhilosophers++;

                // Добавляем новую вилку для нового философа
                forks.add(new ReentrantLock());
                forkColors[newId] = Color.BLACK;

                // Создаем и запускаем нового философа
                Philosopher p = new Philosopher(newId, this);
                philosophers.add(p);
                Thread t = new Thread(p);
                threads.add(t);
                t.start();

                repaint();
                JOptionPane.showMessageDialog(this, "Добавлен Философ " + newId + " и новая вилка.");
            }
        });

        // 3. Создаем и запускаем начальный поток философов
        synchronized (philosophers) {
            for (int i = 0; i < currentNumPhilosophers; i++) {
                Philosopher p = new Philosopher(i, this);
                philosophers.add(p);
                Thread t = new Thread(p);
                threads.add(t);
                t.start();
            }
        }
    }

    // Методы безопасного доступа к вилкам с обновлением цвета для UI
    public int getForksCount() {
        synchronized (philosophers) {
            return forks.size();
        }
    }

    public void lockFork(int index) {
        forks.get(index).lock();
        forkColors[index] = Color.BLUE; // Синий цвет означает, что вилка занята
        repaint();
    }

    public void unlockFork(int index) {
        forkColors[index] = Color.BLACK; // Черный — свободна
        forks.get(index).unlock();
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2 + 20; // Смещаем чуть вниз из-за верхней панели кнопок
        int radius = 150;

        // Рисуем философов и их состояния на основе текущего динамического количества
        int numToDraw = currentNumPhilosophers;

        for (int i = 0; i < numToDraw; i++) {
            // Вычисляем координаты по кругу динамически под текущее число философов
            double angle = 2 * Math.PI * i / numToDraw;
            int x = centerX + (int) (radius * Math.cos(angle)) - 30;
            int y = centerY + (int) (radius * Math.sin(angle)) - 30;

            // Рисуем круг философа
            g2.setColor(philosopherColors[i] != null ? philosopherColors[i] : Color.GREEN);
            g2.fillOval(x, y, 60, 60);
            g2.setColor(Color.BLACK);
            g2.drawOval(x, y, 60, 60);

            // Выводим имя и статус
            g2.drawString("Philosopher " + i, x + 3, y - 22);
            String status = states[i] != null ? states[i] : "Thinking";
            g2.drawString(status, x - 5, y - 5);

            // Отображение счетчика съеденного рядом (требование ТЗ)
            g2.drawString("Meals: " + eatCounters[i], x + 3, y + 75);

            // Рисуем палочки/вилки между философами
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
import javax.swing.*;
import java.awt.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class MainScene extends JPanel {
    protected final int NUM_PHILOSOPHERS = 5;
    protected final Philosopher[] philosophers = new Philosopher[NUM_PHILOSOPHERS];
    protected final Lock[] forks = new ReentrantLock[NUM_PHILOSOPHERS];


    protected final String[] states = new String[NUM_PHILOSOPHERS];
    protected final Color[] philosopherColors = new Color[NUM_PHILOSOPHERS];
    private final Color[] forkColors = new Color[NUM_PHILOSOPHERS];
    protected int[] eatCounters = new int [7];

    public MainScene() {
        setPreferredSize(new java.awt.Dimension(800, 600));
        setBackground(Color.WHITE);

        // 1. Инициализируем замки (вилки)
        for (int i = 0; i < NUM_PHILOSOPHERS; i++) {
            forks[i] = new ReentrantLock();
            forkColors[i] = Color.BLACK; // Свободная вилка — черная
        }

        // 2. Создаем и запускаем потоки философов (СТРОГО ОДИН РАЗ)
        for (int i = 0; i < NUM_PHILOSOPHERS; i++) {
            philosophers[i] = new Philosopher(i, this);
            new Thread(philosophers[i]).start();
        }
    }
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;
        // Включаем сглаживание, чтобы круги и линии были ровными
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;
        int radius = 150; // Радиус стола, вокруг которого сидят философы

        // Рисуем философов и их состояния
        for (int i = 0; i < NUM_PHILOSOPHERS; i++) {
            // Вычисляем координаты для каждого философа по кругу
            double angle = 2 * Math.PI * i / NUM_PHILOSOPHERS;
            int x = centerX + (int) (radius * Math.cos(angle)) - 30;
            int y = centerY + (int) (radius * Math.sin(angle)) - 30;

            // Рисуем круг философа (берём цвет из массива panel.philosopherColors)
            g2.setColor(philosopherColors[i] != null ? philosopherColors[i] : Color.GREEN);
            g2.fillOval(x, y, 60, 60);
            g2.setColor(Color.BLACK);
            g2.drawOval(x, y, 60, 60);

            // Выводим имя философа и его текущий статус (Thinking, Eating и т.д.)
            g2.drawString("Philosoph " + i, x + 3, y - 20);
            String status = states[i] != null ? states[i] : "Thinking";
            g2.drawString(status, x - 5, y - 5);

            // Рисуем палочки/вилки между философами
            double forkAngle = 2 * Math.PI * (i - 0.5) / NUM_PHILOSOPHERS;
            int fx1 = centerX + (int) ((radius - 40) * Math.cos(forkAngle));
            int fy1 = centerY + (int) ((radius - 40) * Math.sin(forkAngle));
            int fx2 = centerX + (int) ((radius + 10) * Math.cos(forkAngle));
            int fy2 = centerY + (int) ((radius + 10) * Math.sin(forkAngle));

            g2.setColor(forkColors[i] != null ? forkColors[i] : Color.BLACK);
            g2.setStroke(new BasicStroke(3)); // Толщина линии вилки
            g2.drawLine(fx1, fy1, fx2, fy2);
        }
    }
    public int getForksCount() {
        // Для обычного массива используем .length вместо .size()
        return forks.length;
    }

    public void lockFork(int index) {
        // Для обычного массива используем квадратные скобки [index] вместо .get(index)
        forks[index].lock();
        forkColors[index] = Color.BLUE;
        repaint();
    }

    public void unlockFork(int index) {
        forkColors[index] = Color.BLACK;
        forks[index].unlock();
        repaint();
    }
    }


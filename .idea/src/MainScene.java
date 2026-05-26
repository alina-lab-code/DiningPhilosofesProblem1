import javax.swing.*;

public class MainScene extends JPanel {
    public MainScene(int x, int y, int width, int height) {
        this.setBounds(x, y, width, height);
        this.setFocusable(true);

        private final int NUM_PHILOSOPHERS = 5;
        private final Philosopher[] philosophers = new Philosopher[NUM_PHILOSOPHERS];
        private final Lock[] forks = new ReentrantLock[NUM_PHILOSOPHERS];


        private final String[] states = new String[NUM_PHILOSOPHERS];
        private final Color[] philosopherColors = new Color[NUM_PHILOSOPHERS];
        private final Color[] forkColors = new Color[NUM_PHILOSOPHERS];
        private final int[] eatCounters = new int[NUM_PHILOSOPHERS];

    public MainScene() {
            setBackground(Color.WHITE);

            for (int i = 0; i < NUM_PHILOSOPHERS; i++) {
                forks[i] = new ReentrantLock();
                forkColors[i] = Color.BLACK; // Свободная вилка — черная
            }

            // Создаем и запускаем потоки философов
            for (int i = 0; i < NUM_PHILOSOPHERS; i++) {
                philosophers[i] = new Philosopher(i);
                new Thread(philosophers[i]).start();
            }
        }
    }
}


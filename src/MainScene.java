import javax.swing.*;
import java.awt.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class MainScene extends JPanel {
    private final int NUM_PHILOSOPHERS = 5;
    private final Philosopher[] philosophers = new Philosopher[NUM_PHILOSOPHERS];
    private final Lock[] forks = new ReentrantLock[NUM_PHILOSOPHERS];


    protected final String[] states = new String[NUM_PHILOSOPHERS];
    protected final Color[] philosopherColors = new Color[NUM_PHILOSOPHERS];
    private final Color[] forkColors = new Color[NUM_PHILOSOPHERS];
    private final int[] eatCounters = new int[NUM_PHILOSOPHERS];

    public MainScene() {
        setBackground(Color.WHITE);


        for (int i = 0; i < NUM_PHILOSOPHERS; i++) {
            forks[i] = new ReentrantLock();
            forkColors[i] = Color.BLACK; // Свободная вилка — черная
        }


        for (int i = 0; i < NUM_PHILOSOPHERS; i++) {
            philosophers[i] = new Philosopher(i, this);
            new Thread(philosophers[i]).start();
        }
    }
}

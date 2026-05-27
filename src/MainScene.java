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


    protected final String[] states = new String[MAX_PHILOSOPHERS];
    protected final Color[] philosopherColors = new Color[MAX_PHILOSOPHERS];
    protected final Color[] forkColors = new Color[MAX_PHILOSOPHERS];
    protected final int[] eatCounters = new int[MAX_PHILOSOPHERS];

    public MainScene() {
        setPreferredSize(new java.awt.Dimension(800, 600));
        setBackground(Color.WHITE);
        setLayout(new BorderLayout());


        for (int i = 0; i < NUM_PHILOSOPHERS; i++) {
            forks[i] = new ReentrantLock();
            forkColors[i] = Color.BLACK;
        }


        JPanel controlPanel = new JPanel();
        controlPanel.setBackground(new Color(235, 240, 245));

        JButton btnStop = new JButton("Stop Philosopher");
        JButton btnResume = new JButton("Renew Philosopher");
        JButton btnAdd = new JButton("Add Philosopher (max +2)");

        controlPanel.add(btnStop);
        controlPanel.add(btnResume);
        controlPanel.add(btnAdd);
        add(controlPanel, BorderLayout.NORTH);


        btnStop.addActionListener(e -> {
            String input = JOptionPane.showInputDialog(this, "Write philosopher ID from (0 to " + (NUM_PHILOSOPHERS - 1) + "):");
            try {
                if (input != null) {
                    int id = Integer.parseInt(input.trim());
                    if (id >= 0 && id < NUM_PHILOSOPHERS) {
                        if (philosophers[id] != null) {
                            philosophers[id].stopPhilosopher();
                            JOptionPane.showMessageDialog(this, "Philosopher stopped " + id);
                        }
                    } else {
                        JOptionPane.showMessageDialog(this, "Philosopher does not exist!");
                    }
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Write a valid number!");
            }
        });


        btnResume.addActionListener(e -> {
            String input = JOptionPane.showInputDialog(this, "Write an ID of philosopher to return to the table (0 to " + (NUM_PHILOSOPHERS - 1) + "):");
            try {
                if (input != null) {
                    int id = Integer.parseInt(input.trim());
                    if (id >= 0 && id < NUM_PHILOSOPHERS) {
                        if (philosophers[id] != null) {

                            if (threads[id] == null || !threads[id].isAlive()) {
                                philosophers[id].startPhilosopher();
                                threads[id] = new Thread(philosophers[id]);
                                threads[id].start();
                                JOptionPane.showMessageDialog(this, "Philosoph " + id + " returned to the table!");
                            } else {
                                JOptionPane.showMessageDialog(this, "Philosoph " + id + " at the table now");
                            }
                        }
                    } else {
                        JOptionPane.showMessageDialog(this, "Invalid ID!");
                    }
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Type a valid number!");
            }
        });


        btnAdd.addActionListener(e -> {
            if (NUM_PHILOSOPHERS >= MAX_PHILOSOPHERS) {
                JOptionPane.showMessageDialog(this, "Limit is reached.You can not add more philosophers!");
                return;
            }

            int newId = NUM_PHILOSOPHERS;


            forks[newId] = new ReentrantLock();
            forkColors[newId] = Color.BLACK;


            philosophers[newId] = new Philosopher(newId, this);
            threads[newId] = new Thread(philosophers[newId]);

            NUM_PHILOSOPHERS++;
            threads[newId].start();

            repaint();
            JOptionPane.showMessageDialog(this, "Philosopher succsessfully added " + newId + " and his right fork too!");
        });


        for (int i = 0; i < NUM_PHILOSOPHERS; i++) {
            philosophers[i] = new Philosopher(i, this);
            threads[i] = new Thread(philosophers[i]);
            threads[i].start();
        }
    }


    public int getForksCount() {
        return NUM_PHILOSOPHERS;
    }

    public void lockFork(int index) {
        forks[index].lock();
        forkColors[index] = Color.RED;
        repaint();
    }

    public void unlockFork(int index) {
        forkColors[index] = Color.GREEN;
        forks[index].unlock();
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2 + 20;
        int radius = 150;

        int numToDraw = NUM_PHILOSOPHERS;

        for (int i = 0; i < numToDraw; i++) {

            double angle = 2 * Math.PI * i / numToDraw;
            int x = centerX + (int) (radius * Math.cos(angle)) - 30;
            int y = centerY + (int) (radius * Math.sin(angle)) - 30;


            g2.setColor(philosopherColors[i] != null ? philosopherColors[i] : Color.GREEN);
            g2.fillOval(x, y, 60, 60);
            g2.setColor(Color.BLACK);
            g2.drawOval(x, y, 60, 60);


            g2.drawString("Philosopher " + i, x + 3, y - 22);
            String status = states[i] != null ? states[i] : "Thinking";
            g2.drawString(status, x - 5, y - 5);


            g2.drawString("Meals: " + eatCounters[i], x + 5, y + 75);


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
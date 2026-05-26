import javax.swing.*;

public class Main {
    public static final int WIDTH = 800;
    public static final int HEIGHT = 600;
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Dining Philosophers Problem");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(WIDTH, HEIGHT);

            MainScene panel = new MainScene();
            frame.add(panel);
            frame.setVisible(true);
        });

    }

    }


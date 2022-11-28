package it.unibo.oop.reactivegui03;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.lang.reflect.InvocationTargetException;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * Third experiment with reactive gui.
 */
public final class AnotherConcurrentGUI extends JFrame {

    private static final long WAITING_TIME = 10_000;
    private static final long serialVersionUID = 1L;
    private static final double WIDTH_PERC = 0.2;
    private static final double HEIGHT_PERC = 0.1;
    private final JLabel display = new JLabel();
    private final JButton stop = new JButton("stop");
    private final JButton up = new JButton("up");
    private final JButton down = new JButton("down");

    /**
    * Builds and displays the GUI.
    */
    public AnotherConcurrentGUI() {

        super();
        final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.setSize((int) (screenSize.getWidth() * WIDTH_PERC), (int) (screenSize.getHeight() * HEIGHT_PERC));
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        final JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());
        panel.add(display);
        panel.add(up);
        panel.add(down);
        panel.add(stop);
        this.getContentPane().add(panel);
        this.setVisible(true);

        final Agent agent = new Agent();
        final Thread th1 = new Thread(agent);
        final Thread th2 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(WAITING_TIME);
                    th1.interrupt();
                    stop.setEnabled(false);
                    up.setEnabled(false);
                    down.setEnabled(false);
                } catch (InterruptedException e) {
                    e.printStackTrace(); // NOPMD: this is just an exercise
                }
            }
        });
        up.addActionListener((e) -> agent.isIncremental = true);
        down.addActionListener((e) -> agent.isIncremental = false);
        stop.addActionListener((e) -> {
            agent.stopCounting();
            stop.setEnabled(false);
            up.setEnabled(false);
            down.setEnabled(false);
        });
        th1.start();
        th2.start();
    }

    private class Agent implements Runnable {

        private volatile boolean stop;
        private int counter;
        private volatile boolean isIncremental = true;

        @Override
        public void run() {
            try {
                while (!this.stop) {
                    final var nextInt = Integer.toString(this.counter);
                    SwingUtilities.invokeAndWait(() -> AnotherConcurrentGUI.this.display.setText(nextInt));
                    if (this.isIncremental) {
                        this.counter++;
                    } else {
                        this.counter--;
                    }
                    Thread.sleep(100);
                }
            } catch (InvocationTargetException | InterruptedException ex) {
                    ex.printStackTrace(); // NOPMD: this is just an exercise
            }
        }

        /**
        * Stops the counter.
        */
        public void stopCounting() {
            this.stop = true;
        }
    }
}

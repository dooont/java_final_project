import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import java.util.Random;

public class Main {
    private static JFrame frame;
    private static JPanel gamePanel;
    private static JPanel settingsPanel;
    private static int difficulty = 1; // Default difficulty level
    private static int score = 0;
    private static JLabel scoreLabel;
    private static JLabel timerLabel;
    private static Timer timer;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }

    // GUI of the program
    private static void createAndShowGUI() {
        frame = new JFrame("Whack A Mole");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 400);
        showMainMenu();
        frame.setVisible(true);
    }

    // Main Menu Screen
    private static void showMainMenu() {
        JPanel mainPanel = new JPanel(new GridLayout(2, 1));
        JButton playButton = new JButton("Play");

        playButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showGameScreen();
            }
        });

        JButton settingsButton = new JButton("Settings");

        settingsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showSettingsScreen();
            }
        });

        mainPanel.add(playButton);
        mainPanel.add(settingsButton);
        frame.getContentPane().removeAll();
        frame.getContentPane().add(mainPanel);
        frame.revalidate();
        frame.repaint();
    }

    // Game Screen
    private static void showGameScreen() {
        gamePanel = new JPanel(new GridLayout(5, 2));
        scoreLabel = new JLabel("Score: " + score);
        gamePanel.add(scoreLabel);

        timerLabel = new JLabel("Time: 60s");
        gamePanel.add(timerLabel);


        // Add mole buttons to the game panel
        for (int i = 0; i < 8; i++) {
            JButton moleButton = new JButton("Mole " + (i+1));
            moleButton.putClientProperty("active", true);

            // when clicking green mole, increase score by 1
            moleButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (moleButton.getBackground() == Color.GREEN) {
                        score++;
                        scoreLabel.setText("Score: " + score);
                    }
                }
            });
            gamePanel.add(moleButton);

            // Start a new thread to change the color of the mole buttons
            Thread moleThread = new Thread(new MoleColorChanger(moleButton));
            moleThread.start();
        }

        // Starts timer, each increasing difficulty decreases time by 10 seconds
        timer = new Timer(60 - (difficulty - 1) * 10, timerLabel);
        timer.start();


        frame.getContentPane().removeAll();
        frame.getContentPane().add(gamePanel);
        frame.revalidate();
        frame.repaint();
    }

    // Settings Screen
    private static void showSettingsScreen() {
        settingsPanel = new JPanel(new GridLayout(4, 1));
        JLabel difficultyLabel = new JLabel("Difficulty: " + difficulty);
        JButton increaseButton = new JButton("+");

        increaseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (difficulty < 3) {
                    difficulty++;
                    difficultyLabel.setText("Difficulty: " + difficulty);
                }
            }
        });

        JButton decreaseButton = new JButton("-");
        decreaseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (difficulty > 1) {
                    difficulty--;
                    difficultyLabel.setText("Difficulty: " + difficulty);
                }
            }
        });

        JButton backButton = new JButton("Back");
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showMainMenu();
            }
        });

        settingsPanel.add(difficultyLabel);
        settingsPanel.add(increaseButton);
        settingsPanel.add(decreaseButton);
        settingsPanel.add(backButton);
        frame.getContentPane().removeAll();
        frame.getContentPane().add(settingsPanel);
        frame.revalidate();
        frame.repaint();
    }

    // Goal is suppose to change the color of moles. Currently not working, try to fix it
    private static class MoleColorChanger implements Runnable {
        private JButton moleButton;
        private Random random;

        public MoleColorChanger(JButton moleButton) {
            this.moleButton = moleButton;
            this.random = new Random();
        }

        @Override
        public void run() {
            while (true) {
                if ((boolean) moleButton.getClientProperty("active")) {
                    int delay = random.nextInt(1000) + 500;

                    // Change color to red
                    moleButton.setBackground(Color.RED);
                    moleButton.setOpaque(true);
                    moleButton.repaint();
                    sleep(delay);

                    // Change color to yellow
                    moleButton.setBackground(Color.YELLOW);
                    moleButton.setOpaque(true);
                    moleButton.repaint();
                    sleep(delay);

                    // Change color to green
                    moleButton.setBackground(Color.GREEN);
                    moleButton.setOpaque(true);
                    moleButton.repaint();
                    sleep(delay);

                }
                else {
                    moleButton.setBackground(null);
                    moleButton.setOpaque(false);
                    moleButton.repaint();
                }
            }
        }
        private void sleep(int delay) {
            try {
                Thread.sleep(delay);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private static class Timer extends Thread {
        private int timeRemaining;
        private JLabel timerLabel;

        public Timer(int initialTime, JLabel timerLabel) {
            this.timeRemaining = initialTime;
            this.timerLabel = timerLabel;
        }

        @Override
        public void run() {
            while (timeRemaining > 0) {
                try {
                    Thread.sleep(1000);
                    timeRemaining--;
                    updateTimerLabel();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            endGame();
        }

        private void updateTimerLabel() {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    timerLabel.setText("Time: " + timeRemaining + "s");
                }
            });
        }

        private void endGame() {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    JOptionPane.showMessageDialog(frame, "Game Over! Your score: " + score);
                    score = 0;
                    showMainMenu();
                }
            });
        }
    }
}

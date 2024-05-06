package whack_a_mole;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import java.util.Random;
import java.util.ArrayList;

public class Whack_A_Mole {
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

        // Start a new thread to change the color of the mole button
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

        public MoleColorChanger(JButton moleButton) {
            this.moleButton = moleButton;
        }

        @Override
        public void run() {
            Random random = new Random();
            while (true) {
                if ((boolean) moleButton.getClientProperty("active")) {
                    moleButton.setBackground(Color.RED);
                    moleButton.repaint();
                    try {
                        Thread.sleep(random.nextInt(1000) + 500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    moleButton.setBackground(Color.YELLOW);
                    moleButton.repaint();
                    try {
                        Thread.sleep(random.nextInt(1000) + 500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    moleButton.setBackground(Color.GREEN);
                    moleButton.repaint();
                    try {
                        Thread.sleep(random.nextInt(1000) + 500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } 
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
                showMainMenu();
            }
        });
    }
}


    
}
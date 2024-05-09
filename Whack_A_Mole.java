package whack_a_mole;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.border.Border;
import java.util.Random;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Statement;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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
        JPanel mainPanel = new JPanel(new GridLayout(3, 1));
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

        JButton howToPlayButton = new JButton("How to Play");

        howToPlayButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showHowToPlayScreen();
            }
        });

        mainPanel.add(playButton);
        mainPanel.add(settingsButton);
        mainPanel.add(howToPlayButton);
        frame.getContentPane().removeAll();
        frame.getContentPane().add(mainPanel);
        frame.revalidate();
        frame.repaint();
    }

    // Game Screen
    private static void showGameScreen() {
        // number of moles increases by 2 based on difficulty
        int numOfMoles = 8 + (difficulty - 1) * 2;
        gamePanel = new JPanel(new GridLayout(numOfMoles / 2 + 1, 2));
        scoreLabel = new JLabel("Score: " + score);
        gamePanel.add(scoreLabel);

        timerLabel = new JLabel("Time: 60s");
        gamePanel.add(timerLabel);


        // Add mole buttons to the game panel
        for (int i = 0; i < numOfMoles; i++) {
            JButton moleButton = new JButton("Mole " + (i+1));
            moleButton.putClientProperty("active", true);

            // when clicking green mole, increase score by 1, if red decrease by 1
            moleButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (moleButton.getBackground() == Color.GREEN) {
                        score++;
                        scoreLabel.setText("Score: " + score);
                    }
                    else if (moleButton.getBackground() == Color.RED) {
                        score--;
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

    // How to Play Screen
    private static void showHowToPlayScreen() {
        JPanel howToPlayPanel = new JPanel(new BorderLayout());
        JTextArea instructionsTextArea = new JTextArea();
        instructionsTextArea.setEditable(false);
        instructionsTextArea.setLineWrap(true);
        instructionsTextArea.setText("How to Play Whack A Mole:\n\n" +
                "1.) Hit a green mole to gain one point, Hit a red mole to lose one point!\n\n" +
                "2.) Increase difficulty to decrease time, increase mole speed and number of moles!\n\n" +
                "3.) Have Fun and don't get too stressed out!");

        JScrollPane scrollPane = new JScrollPane(instructionsTextArea);
        howToPlayPanel.add(scrollPane, BorderLayout.CENTER);

        JButton backButton = new JButton("Back");
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showMainMenu();
            }
        });
        howToPlayPanel.add(backButton, BorderLayout.SOUTH);

        frame.getContentPane().removeAll();
        frame.getContentPane().add(howToPlayPanel);
        frame.revalidate();
        frame.repaint();
    }

    // Changes the moles color, difficulty increases the speed
    private static class MoleColorChanger implements Runnable {
        private JButton moleButton;
        private Random random;
        private int difficulty;

        public MoleColorChanger(JButton moleButton) {
            this.moleButton = moleButton;
            this.random = new Random();
            this.difficulty = difficulty;
        }

        @Override
        public void run() {
            while (true) {
                if ((boolean) moleButton.getClientProperty("active")) {
                    // base color average change rate is 3 seconds, changes depending on difficulty
                    int delay = random.nextInt(3000) - (difficulty - 1) * 1000;

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

//        private void endGame() {
//            SwingUtilities.invokeLater(new Runnable() {
//                @Override
//                public void run() {
//                    JOptionPane.showMessageDialog(frame, "Game Over! Your score: " + score);
//                    score = 0;
//                    showMainMenu();
//                }
//            });
//        }
        private void endGame() {
            SwingUtilities.invokeLater(() -> {
                List<ScoreEntry> topScores = MySQLConnection.getTopScores();
                boolean isTopScore = topScores.size() < 10 || score > topScores.get(topScores.size() - 1).getScore();

                if (isTopScore) {
                    String initials = JOptionPane.showInputDialog(frame, "Congratulations! Enter your initials (3 characters max):");
                    if (initials != null && initials.length() <= 3) {
                        MySQLConnection.insertScore(initials.toUpperCase(), score);
                    }
                } else {
                    MySQLConnection.insertScore("NON", score);
                }

                JOptionPane.showMessageDialog(frame, "Game Over! Your score: " + score);
                score = 0;
                showMainMenu();
            });
    }
    
    class MySQLConnection {
        private static final String URL = "jdbc:mysql://localhost/javafinal?useSSL=false";
        private static final String USERNAME = "root";
        private static final String PASSWORD = "";

        private static Connection getConnection() throws SQLException {
            return DriverManager.getConnection(URL, USERNAME, PASSWORD);
        }

        public static void insertScore(String initials, int score) {
            String sql = "INSERT INTO Scores (initials, score) VALUES (?, ?)";
            try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, initials);
                pstmt.setInt(2, score);
                pstmt.executeUpdate();
            } catch (SQLException e) {
                System.out.println("SQL Exception: " + e.getMessage());
            }
        }

        public static List<ScoreEntry> getTopScores() {
            List<ScoreEntry> scores = new ArrayList<>();
            String sql = "SELECT initials, score FROM Scores ORDER BY score DESC LIMIT 10";
            try (Connection conn = getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    scores.add(new ScoreEntry(rs.getString("initials"), rs.getInt("score")));
                }
            } catch (SQLException e) {
                System.out.println("SQL Exception: " + e.getMessage());
            }
            return scores;
        }
    }

    private static class ScoreEntry {
        private String initials;
        private int score;

        public ScoreEntry(String initials, int score) {
            this.initials = initials;
            this.score = score;
        }

        public String getInitials() {
            return initials;
        }

        public int getScore() {
            return score;
        }
    	}
    }
}
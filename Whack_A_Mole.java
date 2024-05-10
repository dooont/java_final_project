import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.border.Border;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Statement;

import java.sql.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import java.io.*;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

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
        JPanel mainPanel = new JPanel(new GridLayout(4, 1));
        JButton playButton = new JButton("Play");

        playButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showGameModeSelectionScreen();
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

        JButton leaderboardButton = new JButton("Leaderboard");

        leaderboardButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showLeaderboardScreen();
            }
        });

        mainPanel.add(playButton);
        mainPanel.add(settingsButton);
        mainPanel.add(howToPlayButton);
        mainPanel.add(leaderboardButton);
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

    private static void showMultiplayerWaitingScreen() {

        // Thread to establish connectivity between players
        Thread networkThread = new Thread(new Runnable() {
            @Override
            public void run() {
                new Game();
            }
        });
        networkThread.start();

    }

    private static void showGameModeSelectionScreen() {
        JPanel gameModePanel = new JPanel(new GridLayout(3, 1));
        JButton singlePlayerButton = new JButton("Single Player");

        singlePlayerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showGameScreen();
            }
        });

        JButton multiplayerButton = new JButton("Multi Player");

        multiplayerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showMultiplayerWaitingScreen();
            }
        });

        JButton backButton = new JButton("Back");
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showMainMenu();
            }
        });

        gameModePanel.add(singlePlayerButton);
        gameModePanel.add(multiplayerButton);
        gameModePanel.add(backButton);
        frame.getContentPane().removeAll();
        frame.getContentPane().add(gameModePanel);
        frame.revalidate();
        frame.repaint();
    }

    private static void showLeaderboardScreen() {
        JPanel leaderboardPanel = new JPanel(new BorderLayout());
        JTextArea leaderboardTextArea = new JTextArea();
        leaderboardTextArea.setEditable(false);

        List<Timer.ScoreEntry> topScores = Timer.MySQLConnection.getTopScores();
        StringBuilder leaderboardText = new StringBuilder("Leaderboard:\n\n");
        for (int i = 0; i < topScores.size(); i++) {
            Timer.ScoreEntry entry = topScores.get(i);
            leaderboardText.append(String.format("%d, %s - %d\n", i + 1, entry.getInitials(), entry.getScore()));
        }

        leaderboardTextArea.setText(leaderboardText.toString());

        JScrollPane scrollPane = new JScrollPane(leaderboardTextArea);
        leaderboardPanel.add(scrollPane, BorderLayout.CENTER);

        JButton backButton = new JButton("Back");
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showMainMenu();
            }
        });
        leaderboardPanel.add(backButton, BorderLayout.SOUTH);

        frame.getContentPane().removeAll();
        frame.getContentPane().add(leaderboardPanel);
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
    private static class Server {
        private ServerSocket serverSocket;
        private final List<ClientHandler> clients = new ArrayList<>();

        public void start(int port) throws IOException {
            serverSocket = new ServerSocket(port);
            System.out.println("Server started on port " + port);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(clientSocket, this);
                clients.add(clientHandler);
                new Thread(clientHandler).start();
            }
        }

        public void stop() throws IOException {
            for (ClientHandler client : clients) {
                client.stop();
            }
            serverSocket.close();
        }

        public synchronized void broadcastMessage(String message) {
            for (ClientHandler client : clients) {
                client.sendMessage(message);
            }
        }

        private static class ClientHandler implements Runnable {
            private Socket clientSocket;
            private PrintWriter out;
            private BufferedReader in;
            private String clientName;
            private Server server;

            public ClientHandler(Socket socket, Server server) {
                this.clientSocket = socket;
                this.server = server;
            }

            public void run() {
                try {
                    out = new PrintWriter(clientSocket.getOutputStream(), true);
                    in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                    // Get client name
                    out.println("Enter your name:");
                    clientName = in.readLine();
                    server.broadcastMessage(clientName + " has joined the chat!");

                    out.println("Starting game...");
                    server.broadcastMessage("START_GAME");
                    showGameScreen();

                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        if ("quit".equalsIgnoreCase(inputLine)) {
                            break;
                        }
                        server.broadcastMessage(clientName + ": " + inputLine);
                    }

                    server.broadcastMessage(clientName + " has left the chat.");
                } catch (IOException e) {
                    System.out.println("Error handling client " + clientName + ": " + e.getMessage());
                    e.printStackTrace();
                } finally {
                    stop();
                }
            }

            public void stop() {
                try {
                    in.close();
                    out.close();
                    clientSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            public void sendMessage(String message) {
                out.println(message);
            }
        }
    }

    private static class Client {
        private Socket socket;
        private PrintWriter out;
        private BufferedReader in;

        public void startConnection(String ip, int port) throws IOException {
            socket = new Socket(ip, port);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Reading from server
            new Thread(() -> {
                try {
                    String fromServer;
                    while ((fromServer = in.readLine()) != null) {
                        if (fromServer.equals("START_GAME")) {
                            System.out.println("Starting game...");
                            SwingUtilities.invokeLater(this::showGameScreen);
                        }
                        else {
                            System.out.println(fromServer);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();

            // Sending messages to server
            BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
            String userInput;
            while ((userInput = stdIn.readLine()) != null && !userInput.equalsIgnoreCase("quit")) {
                sendMessage(userInput);
            }
            stopConnection();
        }

        private void showGameScreen() {
            Main.showGameScreen();
        }

        public void sendMessage(String message) {
            out.println(message);
        }

        public void stopConnection() throws IOException {
            in.close();
            out.close();
            socket.close();
        }
    }
    private static class Game {
        private JFrame frame;
        private Server server;
        private Client client;
        private boolean isHost = false;

        public Game() {
            frame = new JFrame("Whack A Mole Multiplayer Setup");
            frame.setSize(300, 200);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLayout(new GridLayout(3, 1));

            JButton hostButton = new JButton("Host Game");
            hostButton.addActionListener(this::hostGame);

            JButton joinButton = new JButton("Join Game");
            joinButton.addActionListener(this::joinGame);

            frame.add(hostButton);
            frame.add(joinButton);

            frame.setVisible(true);
        }

        private void hostGame(ActionEvent event) {
            isHost = true;
            server = new Server();
            showHostingScreen();

            try {
                String hostIp = getHostIp();
                if (hostIp != null) {
                    System.out.println("Hosting game on IP: " + hostIp);
                } else {
                    System.out.println("Failed to determine host IP.");
                }
                new Thread(() -> {
                    try {
                        server.start(6666);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }).start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void showHostingScreen() {
            JPanel waitingPanel = new JPanel(new BorderLayout());
            JLabel waitingLabel = new JLabel("Hosting Game...");
            waitingLabel.setHorizontalAlignment(SwingConstants.CENTER);
            waitingPanel.add(waitingLabel, BorderLayout.CENTER);

            JButton cancelButton = new JButton("End Host");
            cancelButton.addActionListener(e -> {
                if (isHost && server != null) {
                    try {
                        server.stop();  // Stop the server if it's running
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                } else if (client != null) {
                    try {
                        client.stopConnection();  // Disconnect the client
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
                frame.dispose();
                showMainMenu();
            });

            waitingPanel.add(cancelButton, BorderLayout.SOUTH);
            frame.getContentPane().removeAll();
            frame.getContentPane().add(waitingPanel);
            frame.revalidate();
            frame.repaint();
        }

        private void joinGame(ActionEvent event) {
            String ip = JOptionPane.showInputDialog(frame, "Enter the IP address of the host:");
            if (ip != null && !ip.isEmpty()) {
                client = new Client();
                // Start client connection on a new thread
                new Thread(() -> {
                    try {
                        client.startConnection(ip, 6666);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }).start();
                frame.dispose();
            }
        }

        private void showWaitingScreen() {
            JPanel waitingPanel = new JPanel(new BorderLayout());
            JLabel waitingLabel = new JLabel("Waiting for other players...");
            waitingLabel.setHorizontalAlignment(SwingConstants.CENTER);
            waitingPanel.add(waitingLabel, BorderLayout.CENTER);

            JButton cancelButton = new JButton("Cancel");
            cancelButton.addActionListener(e -> {
                if (isHost && server != null) {
                    try {
                        server.stop();  // Stop the server if it's running
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                } else if (client != null) {
                    try {
                        client.stopConnection();  // Disconnect the client
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
                frame.dispose();
                showMainMenu();
            });

            waitingPanel.add(cancelButton, BorderLayout.SOUTH);
            frame.getContentPane().removeAll();
            frame.getContentPane().add(waitingPanel);
            frame.revalidate();
            frame.repaint();
        }

        private String getHostIp() {
            try {
                Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
                while (interfaces.hasMoreElements()) {
                    NetworkInterface iface = interfaces.nextElement();
                    if (iface.isLoopback() || !iface.isUp() || iface.isVirtual()) continue;

                    Enumeration<InetAddress> addresses = iface.getInetAddresses();
                    while (addresses.hasMoreElements()) {
                        InetAddress addr = addresses.nextElement();
                        if (!addr.isLoopbackAddress() && addr.isSiteLocalAddress()) {
                            return addr.getHostAddress();
                        }
                    }
                }
            } catch (SocketException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

}

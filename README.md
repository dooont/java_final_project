# Whack-A-Mole Game

A Java-based implementation of the classic Whack-A-Mole arcade game, featuring both single-player and multiplayer modes.

## Creators
- Jack Ma
- Matthew Maung

## Technologies Used
- Java
- AWT
- Swing
- Util
- MySQL

## Features

### Interactive GUI
Navigate through various options including:
- Single-player mode
- Multiplayer mode
- Settings
- How to play

### Gameplay Mechanics
- Moles "pop up" by turning green
- Click green moles to earn points
- Avoid clicking red moles or underground moles (lose points)
- Yellow moles are on standby (no points awarded)

### Database Integration
- Stores top scores and player initials
- Uses MySQL database
- Allows players to enter initials for top 10 scores

### Networking
- Multiplayer functionality
- Server hosting and client connection
- Post-game chat room for players

## Database Setup
Ensure your MySQL configuration matches the following:
- Port: 3306
- Username: root
- Database name: javafinal

### Database Initialization
Run the following SQL query to create the necessary table:

```sql
CREATE TABLE Scores (
    id INT AUTO_INCREMENT PRIMARY KEY,
    initials VARCHAR(3),
    score INT,
    timestamp DATETIME DEFAULT CURRENT_TIMESTAMP
);
```

## How to Play
1. Launch the game and choose your desired mode
2. In single-player, aim for the highest score possible
3. In multiplayer, compete against another player in real-time
4. Click green moles to earn points, avoid red ones
5. After the game, top scorers can enter their initials
6. In multiplayer, optionally join the post-game chat room

Whack A Mole game made with Java using the awt, swing, util, and sql libraries

Creators: Jack Ma and Matthew Maung

For the database to work make sure these information match:
port = 3306
username = root
database name = javafinal 

Relevant Query to insert in database: 

CREATE TABLE Scores (
    id INT AUTO_INCREMENT PRIMARY KEY,
    initials VARCHAR(3),
    score INT,
    timestamp DATETIME DEFAULT CURRENT_TIMESTAMP
);

About Project:

The whack a mole final project is our take on creating whack a mole in java utilizing the libraries and concepts we have learned in CS-UY 3913, Java and Web Design.

Our final project implements three of the concepts we have learned throughout the semester, and are explained in further detail down below:

**Graphics:**
Graphics are implemented throughout our project as the primary form of how the game is played. A GUI is created to navigate through the different options, which include singleplayer, multiplayer, settings, and a "how to play". Once you enter the game (whether it be single player, or through multiplayer), the game begins with moles "popping" up (box turning green). If clicked, the mole will add 1 point to the score kept on the top. If a red mole is clicked or a mole that is "underground", the player loses 1 point. the yellow mole is on standby, and awards no points. 

**JDBC**
The Database is implemented by maintaining a table containing all the scores and "initials" of the players. Players may input their initials (similar to an arcade) if they are one of the top 10 scorers. Once they enter their initials, the score and initials are sent to the database. 

**Networking**
Networking is implemented through the multiplayer function. Once a person hosts a server, other players (preferably 1) can connect to the server, enter their name, and play together. Once they finish, they will have the opportunity to talk in a chatroom post game if they so choose. Otherwise, they can click the x button to exit the chatroom.

Additional Details:

Some videos of us demo-ing the game will also be in the github for your convenience. These will include both working with the settings, as well as showing the multiplayer functionality. 


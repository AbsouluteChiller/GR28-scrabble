import javax.swing.*;
import java.lang.reflect.Array;
import java.util.*;

/**
 * Milestone 2 of the SYSC 3110 Project.
 * A GUI-based playable version of the game "Scrabble", where players play the game through an interactive GUI.
 * This scrabble game is compatible for 2-4 players.
 *
 * @author Michael Kyrollos, 101183521
 * @author Yehan De Silva
 * @author Pathum Danthanarayana, 101181411
 * @version 2.0
 * @date November 11, 2022
 */
public class ScrabbleGameModel extends ScrabbleModel {

    //TODO Remove parser
    private Parser parser;
    /**
     * BoardModel the game is being played on.
     */
    private BoardModel gameBoard;

    /**
     * Players of the scrabble game.
     */
    private List<PlayerModel> players;

    /**
     * The current turn of the scrabble game.
     */
    private int currentTurn;

    /**
     * The current status of the game.
     */
    private boolean gameRunning;

    /**
     * The tile bag used to store all the tiles for this Scrabble game.
     */
    public static final TileBag GAME_TILE_BAG = new TileBag();

    /**
     * The dictionary used to validate words for this Scrabble game.
     */
    public static final ScrabbleDictionary SCRABBLE_DICTIONARY= new ScrabbleDictionary();

    /**
     * Creates a new scrabble game.
     * @author Michael Kyrollos, 101183521
     * @author Yehan De Silva
     * @author Pathum Danthanarayana, 101181411
     * @version 2.1
     * @date October 25, 2022
     */
    public ScrabbleGameModel() {
        //TODO Remove parser
        parser = new Parser();
        gameBoard = new BoardModel(this);
        players = new ArrayList<>();
        // Make the first player in the ArrayList have the first turn
        currentTurn = 0;
        // Start running the game
        gameRunning = true;
    }

    public ScrabbleGameModel(String test) {
        parser = new Parser();
        gameBoard = new BoardModel(this);
        players = new ArrayList<>();
        currentTurn = 0;
        // Start running the game
        gameRunning = true;
    }

    /**
     * Returns the PlayerModel whose currently playing their turn
     *
     * @return the current player
     *
     * @author Yehan De Silva
     * @version 1.0
     * @date November 11, 2022
     */
    public PlayerModel getCurrentPlayer() {
        return players.get(currentTurn % players.size());
    }

    /**
     * Returns the current turn number
     *
     * @return the current turn number
     *
     * @author Yehan De Silva
     * @version 1.0
     * @date November 11, 2022
     */
    public int getCurrentTurn() {
        return currentTurn;
    }

    /**
     * Returns the list of players playing this Scrabble Game.
     * @return List of players
     *
     * @author Yehan De Silva
     * @version 1.0
     * @date November 11, 2022
     */
    public List<PlayerModel> getPlayers() {return this.players;}

    /**
     * Returns the game board this Scrabble game is being played on.
     * @return The game board.
     *
     * @author Yehan De Silva
     * @version 1.0
     * @date November 11, 2022
     */
    public BoardModel getGameBoard() {return this.gameBoard;}

    /**
     * Adds a player to this scrabble game. Only 4 players may be playing at one time.
     * @param playerName Name of player.
     * @return True if player was added, false otherwise.
     * @author Yehan De Silva
     * @version 2.1
     * @date November 11, 2022
     */
    public boolean addPlayer(String playerName) {
        if (players.size() <= 4) {
            players.add(new PlayerModel(playerName, gameBoard));
            return true;
        }
        return false;
    }

    /**
     * TODO Remove parser and text-based implementation of the game.
     * Starts the Scrabble game.
     * @author Michael Kyrollos, 101183521
     * @author Yehan De Silva
     * @author Pathum Danthanarayana, 101181411
     * @version 1.1
     * @date October 25, 2022
     */
    public void play()
    {
        System.out.println("Welcome to Scrabble!");

        // Run the game until a player has ended the game
        while (gameRunning) {

            boolean turnUsed = false;
            // Get the player who has the current turn
            PlayerModel currentPlayer = getCurrentPlayer();

            // Continue prompting the player during their turn for a valid play
            while (!turnUsed)
            {
                // Print the player's name and rack
                System.out.println("\n" + currentPlayer.getName() + "'s turn:\n" + currentPlayer.getRack() + "Enter a command: ");
                // Get and process the player's command
                Command command = parser.getCommand();
                turnUsed = processCommand(command);
            }

            // Determine the next turn
            currentTurn++;
        }
    }


    /**
     * TODO Remove parser and text-based implementation of the game.
     * Process the given command
     *
     * @author Michael Kyrollos, 101183521
     * @author Pathum Danthanarayana, 101181411
     * @version 1.1
     * @date October 25, 2022
     *
     * @param command The command to be processed.
     * @return true If the command ends the game, false otherwise.
     *
     */
    private boolean processCommand(Command command)
    {
        CommandWord commandWord = command.getFirstWord();

        switch (commandWord) {
            case INVALID:
                System.out.println("Enter a valid command");
                return false;

            case HELP:
                this.help();
                return false;

            case PLAY:
                //return this.playWord(command.getSecondWord(), command.getThirdWord()); //temporary change

            case QUIT:
                quitGame();
                return true;

            case REDRAW:
                //redraw(command);
                return true;

            case SKIP:
                return true;
        }
        return false;
    }

    /**
     * Plays a word that was entered by the player, using the "play" button.
     *
     *
     * @param playEvent the PlayWordEvent that was generated to play this word
     * @return true if the word was played successfully, false otherwise
     * @author Amin Zeina, 101186297
     * @date November 12, 2022
     */
    public boolean playWord(PlayWordEvent playEvent) {
        PlayerModel currentPlayer = getCurrentPlayer();
        String word = playEvent.getWord();

        // check that the word is a valid english scrabble word
        if (SCRABBLE_DICTIONARY.validateWord(word)) {
            // check if the word can actually be played
            if (currentPlayer.playWord(playEvent)) {
                JOptionPane.showMessageDialog(null, "You have successfully played \"" +
                        word.toUpperCase() + "\". You now have " + currentPlayer.getScore() + " points!");

                // check if the game should end (rack empty and no tiles in bag)
                if (currentPlayer.getRack().isEmpty() && GAME_TILE_BAG.isEmpty()) {
                    // end game
                    this.endGame();
                }

                this.endTurn();
                return true;
            }
        } else {
            JOptionPane.showMessageDialog(null, "\""+ word.toUpperCase() +
                    "\" is not a valid word. Try again.");
            this.gameBoard.revertBoard();
            currentPlayer.getRack().addTiles(playEvent.getTilesPlaced());
        }
        return false;
    }

    /**
     * Ends the current turn.
     *
     * Switches the current player, updates the turn count, updates the view, enables the new current player's rack
     * tile buttons, and disables the previous player's rack tile buttons.
     *
     * @author Amin Zeina, 101186297
     * @auuthor Yehan De Silva
     * @version 1.1
     * @date November 12, 2022
     */
    public void endTurn() {
        PlayerModel currPlayer = this.getCurrentPlayer();
        for (Tile tile : currPlayer.getRack().getTiles()) {
            tile.setEnabled(false);
        }
        this.currentTurn++;
        currPlayer = this.getCurrentPlayer();
        for (Tile tile : currPlayer.getRack().getTiles()) {
            tile.setEnabled(true);
        }
        updateScrabbleViews();
    }

    /**
     * Sets up the rack tile buttons for the first turn. Disables all players' racks execpt for the first player's
     *
     * @author Amin Zeina, 101186297
     * @version 1.0
     */
    public void setupFirstTurn() {
        // disable rack tile buttons for all but the first player
        for (int i = 1; i < players.size(); i++) {
            for (Tile tile : players.get(i).getRack().getTiles()) {
                tile.setEnabled(false);
            }
        }
    }

    /**
     * Print out help information. list of commands and intro
     *
     * @author Michael Kyrollos, 101183521
     * @version  1.0
     */
    public void help()
    {
        System.out.println("You need help");
        System.out.println("Format to insert word: 'play [word_to_insert] [location_on_board]'");
        System.out.println("Word to insert and location on board must be entered in uppercase");
        System.out.println();
        System.out.println("Your command words are:");
        //TODO Remove parser
        parser.showCommands();
    }

    /**
     * Redraws the given tiles for the current player.
     *
     * @author Michael Kyrollos, 101183521
     * @author Yehan De Silva
     * @version  2.0
     * @date November 13, 2022
     *
     * @param redrawTiles Tiles to be redrawn
     */
    public void redraw(ArrayList<Tile> redrawTiles) {
        this.getCurrentPlayer().redraw(redrawTiles);
        this.endTurn();
    }

    /**
     * Ends the game and displays the winner.
     *
     * @author Amin Zeina, 101186297
     * @version 1.0
     * @date November 13, 2022
     */
    public void endGame() {

        this.calculateEndGameScore();

        ArrayList<PlayerModel> winners = this.determineWinners();
        if (winners.size() == 1) {
            PlayerModel winner = winners.get(0);
            JOptionPane.showMessageDialog(null, "The game has ended.\nPlayer " + winner.getName() +
                    " is the winner!" + " They had " + winner.getScore() + " points.");
        } else {
            String tieMessage = "";
            for (PlayerModel player: winners) {
                tieMessage += "\n" + player.getName() + " - " + player.getScore() + " points";
            }
            JOptionPane.showMessageDialog(null, "The game has ended. The following players have tied:" + tieMessage);
         }

        System.exit(0);
    }

    /**
     * Helper method for endGame method. Calculates the score of the players at the end of the game, following the
     * rules given on the Scrabble wiki:
     *
     * "When the game ends, each player's score is reduced by the sum of their unused
     * letters; in addition, if a player has used all of their letters (known as "going out" or "playing out"),
     * the sum of all other players' unused letters is added to that player's score."
     *
     * @author Amin Zeina, 101186297
     * @version 1.0
     *
     */
    private void calculateEndGameScore() {
        // get the sum of each player's unused letters
        ArrayList<Integer> unusedTileScore = new ArrayList<>(players.size());
        int totalUnusedScore = 0;
        for (PlayerModel player : players) {
            int tempSum = 0;
            for (Tile tile : player.getRack().getTiles()) {
                tempSum += tile.getValue();
            }
            unusedTileScore.add(tempSum);
            totalUnusedScore += tempSum;
        }
        for (int i = 0; i < players.size(); i++) {
            PlayerModel currPlayer = players.get(i);
            if (unusedTileScore.get(i) == 0) {
                // This player has no unused tiles, so add the sum of all other unused tiles to this player's score
                currPlayer.adjustScore(totalUnusedScore);
            } else {
                // This player has unused tiles, so subract the sum of those tiles
                currPlayer.adjustScore(-1 * unusedTileScore.get(i));
            }
        }
    }

    /**
     * Helper method for endGame method. Determines the winner of the game, or "winners" if the game is tied.
     *
     * If there is a tie, multiple players will be returned.
     *
     *
     * @return a list of players at or tied for the highest score.
     */
    private ArrayList<PlayerModel> determineWinners() {
        ArrayList<PlayerModel> winnerList = new ArrayList<>();
        int highestScore = -1;
        for (PlayerModel player : players) {
            int currScore = player.getScore();
            if (currScore > highestScore) {
                winnerList.clear();
                winnerList.add(player);
                highestScore = currScore;
            } else if (currScore == highestScore) {
                winnerList.add(player);
            }
        }
        return winnerList;
    }

    /**
     * Function for quitting game.
     *
     * @author Michael Kyrollos, 101183521
     * @version  1.0
     * @author Pathum Danthanarayana, 101181411
     * @version 1.1
     */
    private void quitGame()
    {
        this.gameRunning = false;
        System.out.println("Thank you. Good bye.");

    }
    public static void main(String[] args) {

        ScrabbleGameModel newGame = new ScrabbleGameModel();
        newGame.play();
    }

}

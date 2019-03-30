package machalica.marcin.tictactoe.client.game;

import javafx.scene.paint.Color;

public class Game {
    private final String playerName;
    private final String opponentName;
    private final char playerSymbol;
    private final char opponentSymbol;
    private final static Color playerColor = Color.valueOf("#0099CC");
    private final static Color opponentColor = Color.valueOf("#DC143C");
    private int playerScore;
    private int opponentScore;
    private boolean isPlayerTurn;
    private char[][] board = new char[3][3];

    public Game(String playerName, String opponentName, boolean isPlayerTurn) {
        this.playerName = playerName;
        this.opponentName = opponentName;
        this.isPlayerTurn = isPlayerTurn;
        this.playerSymbol = isPlayerTurn ? 'x' : 'o';
        this.opponentSymbol = isPlayerTurn ? 'o' : 'x';
    }

    public String getPlayerName() {
        return playerName;
    }

    public String getOpponentName() {
        return opponentName;
    }

    public char getPlayerSymbol() {
        return playerSymbol;
    }

    public char getOpponentSymbol() {
        return opponentSymbol;
    }

    public int getPlayerScore() {
        return playerScore;
    }

    public void incrementPlayerScore(int playerScore) {
        this.playerScore++;
    }

    public int getOpponentScore() {
        return opponentScore;
    }

    public void incrementOpponentScore(int opponentScore) {
        this.opponentScore++;
    }

    public boolean isPlayerTurn() {
        return isPlayerTurn;
    }

    public void setIsPlayerTurn(boolean isPlayerTurn) {
        this.isPlayerTurn = isPlayerTurn;
    }

    public void changeTurn() {
        this.isPlayerTurn = !this.isPlayerTurn;
    }

    public static Color getPlayerColor() {
        return playerColor;
    }

    public static Color getOpponentColor() {
        return opponentColor;
    }

    public void markField(int row, int column) {
        if(row <= 2 && row >= 0 && column <= 2 && column >= 0) {
            if (board[row][column] == '\0') {
                board[row][column] = isPlayerTurn ? playerSymbol : opponentSymbol;
                changeTurn();
            }
        }
    }
}
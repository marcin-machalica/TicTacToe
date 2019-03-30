package machalica.marcin.tictactoe.communication.game;

public class StartGameMessage extends GameMessage {
    private String playerName;
    private String opponentName;
    private boolean isPlayerTurn;

    public StartGameMessage(String playerName, String opponentName, boolean isPlayerTurn) {
        this.playerName = playerName;
        this.opponentName = opponentName;
        this.isPlayerTurn = isPlayerTurn;
    }

    public String getPlayerName() {
        return playerName;
    }

    public String getOpponentName() {
        return opponentName;
    }

    public boolean isPlayerTurn() {
        return isPlayerTurn;
    }
}